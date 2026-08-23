package cn.ykcryobs.ptportal.ptp.parser

import android.util.Log
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import java.nio.ByteBuffer
import java.nio.ByteOrder

object DeviceInfoParser {

    fun parse(data: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            Log.d(PtpConstants.LOG_TAG, "parseDeviceInfo: bufSize=${data.size}")

            buffer.position(buffer.position() + 8) // skip version
            val vendorExtDesc = readPtpString(buffer)
            val functionalMode = buffer.getShort().toInt() and 0xFFFF

            skipPtpArray(buffer, 2) // Operations supported
            skipPtpArray(buffer, 2) // Events supported
            skipPtpArray(buffer, 2) // Device properties supported
            skipPtpArray(buffer, 2) // Capture formats
            skipPtpArray(buffer, 2) // Image formats

            val manufacturer = readPtpString(buffer)
            val deviceName = readPtpString(buffer)
            val deviceVersion = readPtpString(buffer)
            val serialNumber = readPtpString(buffer)

            Log.d(
                PtpConstants.LOG_TAG,
                "parseDeviceInfo: vendor=[$vendorExtDesc], " + "funcMode=0x${
                    functionalMode.toString(16)
                }, " + "mfr=[$manufacturer], model=[$deviceName], fw=[$deviceVersion], " + "sn=[$serialNumber], rem=${buffer.remaining()}"
            )
        } catch (e: Exception) {
            Log.w(PtpConstants.LOG_TAG, "parseDeviceInfo error: ${e.message}", e)
        }
    }

    fun readPtpString(bb: ByteBuffer): String? {
        if (bb.remaining() < 1) return null
        val numChars = bb.get().toInt() and 0xFF
        if (numChars == 0 || bb.remaining() < numChars * 2) return null
        val chars = CharArray(numChars) { bb.getShort().toInt().toChar() }
        return String(chars).trimEnd('\u0000').ifEmpty { null }
    }

    private fun skipPtpArray(bb: ByteBuffer, elementSize: Int) {
        if (bb.remaining() < 4) return
        val count = bb.getInt()
        val skip = count * elementSize
        if (bb.remaining() >= skip) bb.position(bb.position() + skip)
    }
}
