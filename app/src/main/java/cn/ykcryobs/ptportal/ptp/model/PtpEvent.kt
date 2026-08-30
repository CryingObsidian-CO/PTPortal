package cn.ykcryobs.ptportal.ptp.model

data class PtpEvent(
    val eventCode: Int,
    val transactionId: Int,
    val params: List<Int>,
)
