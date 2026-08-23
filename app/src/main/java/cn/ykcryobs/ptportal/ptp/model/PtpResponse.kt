package cn.ykcryobs.ptportal.ptp.model

import cn.ykcryobs.ptportal.ptp.constants.PtpResponseCode

data class PtpResponse(
    val respCode: PtpResponseCode, val transactionId: Int, val params: IntArray = IntArray(0)
) {

    companion object {
        operator fun invoke(
            rawRespCode: Int, transactionId: Int, params: IntArray = IntArray(0)
        ): PtpResponse {
            val code = PtpResponseCode.fromCode(rawRespCode)
            return PtpResponse(code, transactionId, params)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PtpResponse

        if (transactionId != other.transactionId) return false
        if (respCode != other.respCode) return false
        if (!params.contentEquals(other.params)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = transactionId
        result = 31 * result + respCode.hashCode()
        result = 31 * result + params.contentHashCode()
        return result
    }
}
