package cn.ykcryobs.ptportal.ptp

import cn.ykcryobs.ptportal.ptp.constants.PtpResponseCode

data class PtpDataResponse(
    val respCode: PtpResponseCode, val transactionId: Int, val dataPayload: ByteArray
) {
    companion object {
        operator fun invoke(
            rawRespCode: Int, transactionId: Int, dataPayload: ByteArray = ByteArray(0)
        ): PtpDataResponse {
            val code = PtpResponseCode.fromCode(rawRespCode)
            return PtpDataResponse(code, transactionId, dataPayload)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PtpDataResponse

        if (transactionId != other.transactionId) return false
        if (respCode != other.respCode) return false
        if (!dataPayload.contentEquals(other.dataPayload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = transactionId
        result = 31 * result + respCode.hashCode()
        result = 31 * result + dataPayload.contentHashCode()
        return result
    }
}
