package cn.ykcryobs.ptportal.ptp.parser

import android.util.Log
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.model.SdioDisplayStringList
import java.nio.ByteBuffer
import java.nio.ByteOrder

object SdioDisplayStringListParser {

    fun parse(data: ByteArray): SdioDisplayStringList? {
        if (data.size < 8) return null
        try {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            val offset = (buffer.int.toLong() and 0xFFFFFFFFL) - 8
            val size = (buffer.int.toLong() and 0xFFFFFFFFL)

            // Reserved: 从当前位置到 offset 的字节
            val reservedSize = offset.coerceAtMost(buffer.remaining().toLong()).toInt()
            val reserved = ByteArray(reservedSize)
            if (reservedSize > 0) {
                buffer.get(reserved)
            }

            // 解析 DisplayStringList Binary
            val strings = parseStringList(buffer)

            Log.d(
                PtpConstants.LOG_TAG,
                "SDIDisplayStringList 解析完成: offset=$offset, size=$size, strings=${strings.size}"
            )
            return SdioDisplayStringList(strings)
        } catch (e: Exception) {
            Log.e(PtpConstants.LOG_TAG, "SDIDisplayStringList 解析异常: ${e.message}", e)
            return null
        }
    }

    private fun parseStringList(buffer: ByteBuffer): List<String> {
        val strings = mutableListOf<String>()
        while (buffer.remaining() > 0) {
            // 读取字符数 (UInt8)
            if (buffer.remaining() < 1) break
            val numChars = buffer.get().toInt() and 0xFF
            if (numChars == 0) break // 空字符串可能表示结束

            // 读取 UTF-16LE 字符
            if (buffer.remaining() < numChars * 2) break
            val chars = CharArray(numChars) { buffer.short.toInt().toChar() }
            strings.add(String(chars).trimEnd('\u0000'))
        }
        return strings
    }
}
