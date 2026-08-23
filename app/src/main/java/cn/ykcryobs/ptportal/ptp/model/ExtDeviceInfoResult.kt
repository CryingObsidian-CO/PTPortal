package cn.ykcryobs.ptportal.ptp.model

sealed class ExtDeviceInfoResult {
    data class Success(val version: Int) : ExtDeviceInfoResult()
    data class VersionMismatch(val expected: Int, val actual: Int) : ExtDeviceInfoResult()
    object ResponseError : ExtDeviceInfoResult()
    object DataTooShort : ExtDeviceInfoResult()
}