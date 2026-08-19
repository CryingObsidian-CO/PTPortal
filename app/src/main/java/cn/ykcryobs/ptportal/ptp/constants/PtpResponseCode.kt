package cn.ykcryobs.ptportal.ptp.constants

sealed class PtpResponseCode {

    /** 标准已知PTP响应码枚举 */
    enum class Code(val code: Int) {
        OK(0x2001),                // 执行成功
        GENERAL_ERROR(0x2002),     // 通用错误
        SESSION_NOT_OPEN(0x2003),  // session 未打开
        PARAMETER_NOT_SUPPORTED(0x2006), // 参数不支持
        ACCESS_DENIED(0x200F),     // 无权限/被拒绝
        DEVICE_BUSY(0x2019);       // 设备正忙
    }

    data class Known(val value: Code) : PtpResponseCode()
    data class Unknown(val rawCode: Int) : PtpResponseCode()

    companion object {
        fun fromCode(code: Int): PtpResponseCode {
            val matched = Code.entries.find { it.code == code }
            return matched?.let { Known(it) } ?: Unknown(code)
        }

        val UNKNOWN get() = Unknown(-1)

        val OK get() = Known(Code.OK)
        val GENERAL_ERROR get() = Known(Code.GENERAL_ERROR)
        val SESSION_NOT_OPEN get() = Known(Code.SESSION_NOT_OPEN)
        val PARAMETER_NOT_SUPPORTED get() = Known(Code.PARAMETER_NOT_SUPPORTED)
        val ACCESS_DENIED get() = Known(Code.ACCESS_DENIED)
        val DEVICE_BUSY get() = Known(Code.DEVICE_BUSY)
    }

    val code: Int
        get() = when (this) {
            is Known -> value.code
            is Unknown -> rawCode
        }
}
