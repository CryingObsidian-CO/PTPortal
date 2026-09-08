package cn.ykcryobs.ptportal.ptp.model

import cn.ykcryobs.ptportal.ptp.constants.PtpEventCode
import java.util.concurrent.atomic.AtomicLong

sealed class PtpEvent {
    val eventId: Long = eventIdGenerator.getAndIncrement()
    val timestamp: Long = System.currentTimeMillis()
    abstract val eventCode: PtpEventCode

    companion object {
        private val eventIdGenerator = AtomicLong(0)
    }
}

data class RawPtpEvent(
    val rawCode: Int, val params: IntArray
) : PtpEvent() {
    override val eventCode: PtpEventCode =
        PtpEventCode.fromCode(rawCode) ?: error("Unknown event code: $rawCode")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawPtpEvent

        if (rawCode != other.rawCode) return false
        if (!params.contentEquals(other.params)) return false
        if (eventCode != other.eventCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawCode.hashCode()
        result = 31 * result + params.contentHashCode()
        result = 31 * result + eventCode.hashCode()
        return result
    }
}