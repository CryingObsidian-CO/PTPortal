package cn.ykcryobs.ptportal.ptp.constants

enum class PtpStandardOpCode(val code: Int) {

    GET_DEVICE_INFO(0x1001),// 读取设备信息
    OPEN_SESSION(0x1002),// 打开 PTP 会话
    CLOSE_SESSION(0x1003),// 关闭 PTP 会话

    SDIO_CONNECT(0x9201),// SDIO 连接
    SDIO_GET_EXT_DEVICE_INFO(0x9202), //获取扩展设备信息
    SDIO_GET_ALL_EXT_DEVICE_PROP_INFO(0x9209), //获取所有设备属性
    SDIO_GET_VENDOR_CODE_VERSION(0x9216); // 获取实际 SDIO 版本

    companion object {
        fun fromCode(code: Int): PtpStandardOpCode? {
            return entries.find { it.code == code }
        }
    }

    fun getShort(): Short {
        return this.code.toShort()
    }

}