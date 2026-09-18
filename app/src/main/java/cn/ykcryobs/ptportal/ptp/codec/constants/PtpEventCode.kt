package cn.ykcryobs.ptportal.ptp.codec.constants

enum class PtpEventCode(val code: Int) {
    STORE_ADDED(0x4004),
    STORE_REMOVED(0xC202),
    SDIE_DEVICE_PROP_CHANGED(0xC203),
    DEVICE_PROP_CHANGED(0xC225);

    companion object {
        fun fromCode(code: Int): PtpEventCode? = entries.find { it.code == code }
    }
}