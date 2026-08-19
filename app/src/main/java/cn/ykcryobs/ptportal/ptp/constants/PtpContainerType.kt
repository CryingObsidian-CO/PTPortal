package cn.ykcryobs.ptportal.ptp.constants

enum class PtpContainerType(val code: Int) {
    COMMAND(1),      // 命令包：主机发给相机的指令
    DATA(2),         // 数据包：传输具体数据（比如设备信息、照片文件）
    RESPONSE(3),     // 响应包：相机对命令的执行结果
    EVENT(4),        // 事件包：相机主动上报的状态变化

    UNKNOWN_TYPE(0);

    companion object {
        fun fromCode(code: Int): PtpContainerType {
            return entries.find { it.code == code } ?: UNKNOWN_TYPE
        }
    }

    fun getShort(): Short {
        return this.code.toShort()
    }
}