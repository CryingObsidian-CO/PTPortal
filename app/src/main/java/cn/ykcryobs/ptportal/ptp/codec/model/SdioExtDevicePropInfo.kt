package cn.ykcryobs.ptportal.ptp.codec.model

data class SdioExtDevicePropInfo(
    val numOfElements: Long,
    val properties: List<DevicePropInfo>,
)