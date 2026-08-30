package cn.ykcryobs.ptportal.ptp.parser

import android.util.Log
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.model.ParsedStorageInfo
import java.nio.ByteBuffer
import java.nio.ByteOrder

object StorageInfoParser {

    fun parse(data: ByteArray): ParsedStorageInfo? {
        if (data.size < 26) return null
        return try {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            val storageType = buffer.short.toInt() and 0xFFFF
            val filesystemType = buffer.short.toInt() and 0xFFFF
            val accessCapability = buffer.short.toInt() and 0xFFFF
            val maxCapacityBytes = buffer.long
            val freeSpaceBytes = buffer.long
            val freeSpaceInImages = buffer.int
            val description = DeviceInfoParser.readPtpString(buffer)
            val volumeLabel = DeviceInfoParser.readPtpString(buffer)
            ParsedStorageInfo(
                storageType,
                filesystemType,
                accessCapability,
                maxCapacityBytes,
                freeSpaceBytes,
                freeSpaceInImages,
                description,
                volumeLabel,
            )
        } catch (e: Exception) {
            Log.w(PtpConstants.LOG_TAG, "parseStorageInfo error: ${e.message}", e)
            null
        }
    }

    fun parseStorageIds(data: ByteArray): List<Int>? {
        if (data.size < 4) return null
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        val count = buffer.int
        val ids = mutableListOf<Int>()
        repeat(count) {
            if (buffer.remaining() >= 4) ids += buffer.int else return ids
        }
        return ids
    }
}
