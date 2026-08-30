package cn.ykcryobs.ptportal.ptp.model

/** GetStorageInfo (0x1005) 解析结果，容量字段为原始字节数。 */
data class ParsedStorageInfo(
    val storageType: Int,
    val filesystemType: Int,
    val accessCapability: Int,
    val maxCapacityBytes: Long,
    val freeSpaceBytes: Long,
    val freeSpaceInImages: Int,
    val description: String?,
    val volumeLabel: String?,
)
