package cn.ykcryobs.ptportal.ptp

import cn.ykcryobs.ptportal.ptp.constants.PtpEventCode
import cn.ykcryobs.ptportal.ptp.model.PtpEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.toList
import kotlin.concurrent.read
import kotlin.concurrent.write

class EventManager private constructor() {
    private val eventQueue = LinkedBlockingQueue<PtpEvent>()

    private val eventExecutor: ExecutorService = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "PtpEventDispatcher").apply {
            isDaemon = true
            priority = Thread.NORM_PRIORITY
        }
    }

    private val subscribers = ConcurrentHashMap<PtpEventCode, MutableList<EventCallback<*>>>()
    private val lock = ReentrantReadWriteLock()

    @Volatile
    private var isRunning = false

    fun interface EventCallback<T : PtpEvent> {
        fun onEvent(event: T)
    }

    init {
        startDispatcher()
    }

    private fun startDispatcher() {
        if (isRunning) return
        isRunning = true
        eventExecutor.submit {
            while (isRunning && !eventExecutor.isShutdown) {
                try {
                    val event = eventQueue.take()
                    dispatchEvent(event)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun postEvent(event: PtpEvent) {
        if (!isRunning) return
        eventQueue.offer(event)
    }

    fun <T : PtpEvent> subscribe(eventCode: PtpEventCode, callback: EventCallback<T>) {
        lock.write {
            val list = subscribers.getOrPut(eventCode) { CopyOnWriteArrayList() }
            list.add(callback)
        }
    }

    fun <T : PtpEvent> unsubscribe(eventCode: PtpEventCode, callback: EventCallback<T>) {
        lock.write {
            subscribers[eventCode]?.remove(callback)
        }
    }

    private fun dispatchEvent(event: PtpEvent) {
        val callbacks = lock.read {
            subscribers[event.eventCode]?.toList() ?: emptyList()

        }
        callbacks.forEach { callback ->
            try {
                @Suppress("UNCHECKED_CAST") (callback as EventCallback<PtpEvent>).onEvent(event)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun awaitEvent(eventCode: PtpEventCode, timeoutMs: Long = 5000): PtpEvent? {
        val latch = CountDownLatch(1)
        var result: PtpEvent? = null

        val tempCallback = EventCallback<PtpEvent> { event ->
            result = event
            latch.countDown()
        }

        subscribe(eventCode, tempCallback)
        return try {
            if (latch.await(timeoutMs, TimeUnit.MILLISECONDS)) result else null
        } finally {
            unsubscribe(eventCode, tempCallback)
        }
    }

    fun shutdown() {
        isRunning = false
        eventExecutor.shutdownNow()
        eventQueue.clear()
        subscribers.clear()
    }

    companion object {
        val instance: EventManager by lazy { EventManager() }
    }
}