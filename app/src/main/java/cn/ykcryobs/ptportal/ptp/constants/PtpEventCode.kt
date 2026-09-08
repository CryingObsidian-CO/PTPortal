package cn.ykcryobs.ptportal.ptp.constants

enum class PtpEventCode(val code: Int) {
    STORE_ADDED(0x4004), STORE_REMOVED(0xC202), DEVICE_PROP_CHANGED(0xC225);

    companion object {
        fun fromCode(code: Int): PtpEventCode? = entries.find { it.code == code }
    }
}