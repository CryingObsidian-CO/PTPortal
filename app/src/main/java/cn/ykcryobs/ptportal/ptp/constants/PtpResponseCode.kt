package cn.ykcryobs.ptportal.ptp.constants

sealed interface PtpRespCode {
    val code: Int

    data class Unknown(val rawCode: Int) : PtpRespCode {
        override val code: Int get() = rawCode
    }
}

enum class PtpResponseCode(override val code: Int) : PtpRespCode {
    OK(0x2001),                // 执行成功
    GENERAL_ERROR(0x2002),     // 通用错误
    SESSION_NOT_OPEN(0x2003),  // session 未打开
    PARAMETER_NOT_SUPPORTED(0x2006), // 参数不支持
    ACCESS_DENIED(0x200F),     // 无权限/被拒绝
    DEVICE_BUSY(0x2019),       // 设备正忙
    SESSION_ALREADY_OPEN(0x201E), // session 已打开
    STORE_NOT_AVAILABLE(0x2013);  // 存储不可用

    companion object {
        fun fromCode(code: Int): PtpRespCode =
            entries.find { it.code == code } ?: PtpRespCode.Unknown(code)
    }
}
