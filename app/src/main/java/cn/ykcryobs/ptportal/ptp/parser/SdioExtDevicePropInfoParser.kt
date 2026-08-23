package cn.ykcryobs.ptportal.ptp.parser

import android.util.Log
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.model.DevicePropInfo
import cn.ykcryobs.ptportal.ptp.model.IsEnabled
import cn.ykcryobs.ptportal.ptp.model.SdiExtDevicePropInfo
import java.nio.ByteBuffer
import java.nio.ByteOrder

object SdioExtDevicePropInfoParser {

    fun parse(data: ByteArray): SdiExtDevicePropInfo? {
        if (data.size < 8) return null
        try {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            val numOfElements = buffer.long
            val properties = mutableListOf<DevicePropInfo>()

            for (i in 0 until numOfElements) {
                if (buffer.remaining() < 7) {
                    Log.w(
                        PtpConstants.LOG_TAG,
                        "剩余字节不足，提前终止解析，终止在：i=$i（应当终止位置：${numOfElements - 1}）"
                    )
                    break
                }
                val prop = parseOne(buffer) ?: break
                properties.add(prop)
            }

            Log.d(
                PtpConstants.LOG_TAG,
                "SdiExtDevicePropInfo 解析完成: numOfElements=$numOfElements, parsed=${properties.size}"
            )
            return SdiExtDevicePropInfo(numOfElements, properties)
        } catch (e: Exception) {
            Log.e(PtpConstants.LOG_TAG, "SdiExtDevicePropInfo 解析异常: ${e.message}", e)
            return null
        }
    }

    private fun parseOne(buffer: ByteBuffer): DevicePropInfo? {
        val propertyCode = buffer.short.toInt() and 0xFFFF
        val dataType = buffer.short.toInt() and 0xFFFF
        val getSet = buffer.get().toInt() and 0xFF
        val isEnable = buffer.get().toInt() and 0xFF

        val isArray = dataType in 0x4001..0x400A
        val scalarDt = if (isArray) dataType and 0x0FFF else dataType

        val defaultValue: Long
        val currentValue: Long

        if (isArray) {
            defaultValue = readArray(buffer, scalarDt) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "parseOne:  读取默认值失败，放弃本次数据，propCode=0x${propertyCode.toString(16)}, dataType=0x${
                        dataType.toString(16)
                    }"
                )
                return null
            }
            currentValue = readArray(buffer, scalarDt) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "parseOne: 读取当前值失败，放弃本次数据，propCode=0x${propertyCode.toString(16)}, dataType=0x${
                        dataType.toString(16)
                    }"
                )
                return null
            }
        } else {
            defaultValue = readScalar(buffer, scalarDt) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "parseOne: 读取默认值失败，放弃本次数据，propCode=0x${propertyCode.toString(16)}, dataType=0x${
                        dataType.toString(16)
                    }"
                )
                return null
            }
            currentValue = readScalar(buffer, scalarDt) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "parseOne: 读取当前值失败，放弃本次数据，propCode=0x${propertyCode.toString(16)}, dataType=0x${
                        dataType.toString(16)
                    }"
                )
                return null
            }
        }

        if (buffer.remaining() < 1) {
            Log.e(
                PtpConstants.LOG_TAG,
                "parseOne: 剩余长度不足，放弃本次数据，propCode=0x${propertyCode.toString(16)}, remain=${buffer.remaining()}"
            )
            return null
        }
        val formFlag = buffer.get().toInt() and 0xFF

        var setValues = emptyList<Long>()
        var getSetValues = emptyList<Long>()
        if (formFlag == 0x01) {
            val scalarSize = dataTypeSize(scalarDt)
            val skipBytes = scalarSize * 3
            if (buffer.remaining() < skipBytes) {
                buffer.position(buffer.position() + buffer.remaining())
            } else {
                buffer.position(buffer.position() + skipBytes)
            }
        } else if (formFlag == 0x02) {
            setValues = readEnumList(buffer, scalarDt) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG, "parseOne FORM_ENUM: 读取 Set‑Only 列表失败, propCode=0x${
                        propertyCode.toString(16)
                    }"
                )
                return null
            }
            getSetValues = readEnumList(buffer, scalarDt) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "parseOne FORM_ENUM: 读取 GetSet enum 列表失败, propCode=0x${
                        propertyCode.toString(16)
                    }"
                )
                return null
            }
        }

        return DevicePropInfo(
            propertyCode,
            dataType,
            getSet == 1,
            IsEnabled.fromCode(isEnable),
            defaultValue,
            currentValue,
            setValues,
            getSetValues,
        )
    }

    private fun readScalar(buffer: ByteBuffer, dataType: Int): Long? {
        return when (dataType) {
            0x0001 -> buffer.get().toLong()                          // INT8
            0x0002 -> (buffer.get().toInt() and 0xFF).toLong()       // UINT8
            0x0003 -> buffer.short.toLong()                           // INT16
            0x0004 -> (buffer.short.toInt() and 0xFFFF).toLong()     // UINT16
            0x0005 -> buffer.int.toLong()                             // INT32
            0x0006 -> (buffer.int.toLong() and 0xFFFFFFFFL)          // UINT32
            0x0007 -> buffer.long                                     // INT64
            0x0008 -> buffer.long                                     // UINT64
            0xFFFF -> { // STR: uint16 length + UTF-16LE chars
                val len = buffer.get().toInt() and 0xFFFF
                var skip = len * 2

                if (buffer.remaining() < skip) {
                    skip = buffer.remaining()
                }
                buffer.position(buffer.position() + skip)
                0L
            }

            else -> {
                Log.e(
                    PtpConstants.LOG_TAG, "readScalar 未知的数据类型=0x${dataType.toString(16)}"
                )
                null
            }
        }
    }

    private fun readArray(buffer: ByteBuffer, elementDt: Int): Long? {
        if (buffer.remaining() < 4) {
            Log.e(PtpConstants.LOG_TAG, "readArray: buffer 长度不足")
            return null
        }
        val count = buffer.int.toLong() and 0xFFFFFFFFL
        val elemSize = dataTypeSize(elementDt)
        if (elemSize <= 0) {
            Log.e(PtpConstants.LOG_TAG, "readArray: 未知的数据类型=0x${elementDt.toString(16)}")
            return null
        }
        val skip = count * elemSize
        // NOTE 暂时没有这类数据，所以直接跳过读取，消费掉 buffer 就可以
        if (buffer.remaining() < skip) {
            buffer.position(buffer.position() + buffer.remaining())
        } else {
            buffer.position(buffer.position() + skip.toInt())
        }
        return 0L
    }

    private fun readEnumList(buffer: ByteBuffer, scalarDt: Int): List<Long>? {
        if (buffer.remaining() < 2) if (buffer.remaining() < 2) {
            Log.e(PtpConstants.LOG_TAG, "readEnumList: buffer 长度不足")
            return null
        }
        val count = buffer.short.toInt() and 0xFFFF
        val values = mutableListOf<Long>()
        repeat(count) { idx ->
            val item = readScalar(buffer, scalarDt)
            if (item == null) {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "readEnumList: readScalar fail at index=$idx, scalarDt=0x${scalarDt.toString(16)}"
                )
                return null
            }
            values.add(item)
        }
        return values
    }

    private fun dataTypeSize(dt: Int): Int = when (dt) {
        0x0001, 0x0002 -> 1
        0x0003, 0x0004 -> 2
        0x0005, 0x0006 -> 4
        0x0007, 0x0008 -> 8
        else -> 0
    }
}
