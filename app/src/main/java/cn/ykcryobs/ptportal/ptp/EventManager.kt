package cn.ykcryobs.ptportal.ptp

import cn.ykcryobs.ptportal.ptp.model.PtpEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.toList
import kotlin.reflect.KClass


class EventManager private constructor() {
    private val subscribers: ConcurrentHashMap<Class<*>, MutableSet<EventCallback<*>>> =
        ConcurrentHashMap()
    private var isShutdown: Boolean = false

    // 固定使用 Dispatchers.IO，不再从外部传入 dispatcher
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun <T : PtpEvent> subscribe(clazz: KClass<T>, callback: EventCallback<T>) {
        if (isShutdown) return
        val set = subscribers.getOrPut(clazz.java) { ConcurrentHashMap.newKeySet() }
        set.add(callback)
    }

    fun <T : PtpEvent> subscribe(clazz: Class<T>, callback: EventCallback<T>) {
        subscribe(clazz.kotlin, callback)
    }

    fun <T : PtpEvent> unsubscribe(clazz: KClass<T>, callback: EventCallback<T>) {
        val set = subscribers[clazz.java] ?: return
        set.remove(callback)
        if (set.isEmpty()) {
            subscribers.remove(clazz.java)
        }
    }

    fun <T : PtpEvent> unsubscribe(clazz: Class<T>, callback: EventCallback<T>) {
        unsubscribe(clazz.kotlin, callback)
    }

    fun postEvent(event: PtpEvent) {
        if (isShutdown) return
        scope.launch {
            val targetClass = event::class.java
            val callbacks = subscribers[targetClass]?.toList() ?: return@launch
            for (cb in callbacks) {
                try {
                    @Suppress("UNCHECKED_CAST") (cb as EventCallback<PtpEvent>).onEvent(event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun shutdown() {
        if (isShutdown) return
        isShutdown = true
        scope.cancel()
        subscribers.clear()
    }

    fun interface EventCallback<T : PtpEvent> {
        fun onEvent(event: T)
    }

    fun isClosed(): Boolean = isShutdown

    companion object {
        val instance: EventManager by lazy { EventManager() }
    }
}