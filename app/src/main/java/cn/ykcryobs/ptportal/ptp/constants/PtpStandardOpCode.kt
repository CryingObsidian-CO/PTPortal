package cn.ykcryobs.ptportal.ptp.constants

enum class PtpStandardOpCode(val code: Int) {

    GET_DEVICE_INFO(0x1001),// 读取设备信息
    OPEN_SESSION(0x1002),// 打开 PTP 会话
    CLOSE_SESSION(0x1003),// 关闭 PTP 会话
    GET_STORAGE_IDS(0x1004), // 读取存储 ID 列表
    GET_STORAGE_INFO(0x1005), // 读取单个存储信息（容量等）

    SDIO_CONNECT(0x9201),// SDIO 连接
    SDIO_GET_EXT_DEVICE_INFO(0x9202), //获取扩展设备信息
    SDIO_GET_ALL_EXT_DEVICE_PROP_INFO(0x9209), //获取所有设备属性
    SDIO_OPEN_SESSION(0x9210), // 开启 sdio 会话
    SDIO_SET_CONTENTS_TRANSFER_MODE(0x9212), // 开启/关闭内容传输模式
    SDIO_GET_DISPLAY_STRING_LIST(0x9215), // 获取显示字符串列表
    SDIO_GET_VENDOR_CODE_VERSION(0x9216), // 获取实际 SDIO 版本
    SDIO_GET_EXT_DEVICE_PROP(0x9251); // 按 DevicePropCode 获取单个设备属性

    companion object {
        fun fromCode(code: Int): PtpStandardOpCode? {
            return entries.find { it.code == code }
        }
    }

    fun getShort(): Short {
        return this.code.toShort()
    }

}
