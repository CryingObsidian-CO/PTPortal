package cn.ykcryobs.ptportal.ptp.parser

import android.util.Log
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.model.DevicePropInfo
import cn.ykcryobs.ptportal.ptp.model.IsEnabled
import cn.ykcryobs.ptportal.ptp.model.PropForm
import cn.ykcryobs.ptportal.ptp.model.PropValue
import cn.ykcryobs.ptportal.ptp.model.SdioExtDevicePropInfo
import java.nio.ByteBuffer
import java.nio.ByteOrder

object SdioExtDevicePropInfoParser {

    private const val TYPE_STR = 0xFFFF
    private const val TYPE_ARRAY_MIN = 0x4001
    private const val TYPE_ARRAY_MAX = 0x400A

    fun parse(data: ByteArray): SdioExtDevicePropInfo? {
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
            return SdioExtDevicePropInfo(numOfElements, properties)
        } catch (e: Exception) {
            Log.e(PtpConstants.LOG_TAG, "SdiExtDevicePropInfo 解析异常: ${e.message}", e)
            return null
        }
    }

    private fun parseOne(buffer: ByteBuffer): DevicePropInfo? {
        val propertyCode = buffer.short.toInt() and 0xFFFF
        val dataType = buffer.short.toInt() and 0xFFFF
        val getSetFlag = buffer.get().toInt() and 0xFF
        val isEnable = buffer.get().toInt() and 0xFF

        val scalarDt =
            if (dataType in TYPE_ARRAY_MIN..TYPE_ARRAY_MAX) dataType and 0x0FFF else dataType

        fun readValueOrFail(what: String): PropValue? = readValue(buffer, dataType) ?: run {
            Log.e(
                PtpConstants.LOG_TAG,
                "parseOne: 读取${what}失败，放弃本次数据，propCode=0x${propertyCode.toString(16)}, dataType=0x${
                    dataType.toString(16)
                }"
            )
            null
        }

        val defaultValue = readValueOrFail("默认值") ?: return null
        val currentValue = readValueOrFail("当前值") ?: return null

        if (buffer.remaining() < 1) {
            Log.e(
                PtpConstants.LOG_TAG,
                "parseOne: 剩余长度不足，放弃本次数据，propCode=0x${propertyCode.toString(16)}, remain=${buffer.remaining()}"
            )
            return null
        }
        val form = when (val formFlag = buffer.get().toInt() and 0xFF) {
            0x01 -> {
                val min = readScalar(buffer, scalarDt)
                val step = readScalar(buffer, scalarDt)
                val max = readScalar(buffer, scalarDt)
                if (min == null || step == null || max == null) {
                    Log.e(
                        PtpConstants.LOG_TAG,
                        "parseOne FORM_RANGE: 读取 min/step/max 失败, propCode=0x${
                            propertyCode.toString(
                                16
                            )
                        }"
                    )
                    return null
                }
                PropForm.Range(min, step, max)
            }

            0x02 -> {
                val setOnly = readValueList(buffer, dataType) ?: run {
                    Log.e(
                        PtpConstants.LOG_TAG,
                        "parseOne FORM_ENUM: 读取 Set‑Only 列表失败, propCode=0x${
                            propertyCode.toString(16)
                        }"
                    )
                    return null
                }
                val getSet = readValueList(buffer, dataType) ?: run {
                    Log.e(
                        PtpConstants.LOG_TAG,
                        "parseOne FORM_ENUM: 读取 GetSet enum 列表失败, propCode=0x${
                            propertyCode.toString(16)
                        }"
                    )
                    return null
                }
                PropForm.EnumSet(setOnly, getSet)
            }

            else -> {
                if (formFlag != 0x00) {
                    Log.w(
                        PtpConstants.LOG_TAG,
                        "parseOne: 未知 formFlag=$formFlag, propCode=0x${propertyCode.toString(16)}"
                    )
                }
                PropForm.None
            }
        }

        return DevicePropInfo(
            propertyCode,
            dataType,
            getSetFlag == 1,
            IsEnabled.fromCode(isEnable),
            defaultValue,
            currentValue,
            form,
        )
    }

    /** 按 dataType 分派读取一个完整属性值：字符串 / 数组 / 标量 */
    private fun readValue(buffer: ByteBuffer, dataType: Int): PropValue? = when {
        dataType == TYPE_STR -> readStr(buffer)?.let { PropValue.Str(it) }
        dataType in TYPE_ARRAY_MIN..TYPE_ARRAY_MAX -> readArrayValue(buffer, dataType and 0x0FFF)
        else -> readScalar(buffer, dataType)?.let { PropValue.Scalar(it) }
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

            else -> {
                Log.e(
                    PtpConstants.LOG_TAG, "readScalar 未知的数据类型=0x${dataType.toString(16)}"
                )
                null
            }
        }
    }

    /** PTP 字符串：UInt8 字符数 + UTF-16LE 字符序列 */
    private fun readStr(buffer: ByteBuffer): String? {
        if (buffer.remaining() < 1) return null
        val numChars = buffer.get().toInt() and 0xFF
        if (buffer.remaining() < numChars * 2) return null
        val chars = CharArray(numChars) { buffer.short.toInt().toChar() }
        return String(chars).trimEnd('\u0000')
    }

    /** 数组：UInt32 元素个数 + 元素序列 */
    private fun readArrayValue(buffer: ByteBuffer, elementDt: Int): PropValue? {
        if (buffer.remaining() < 4) {
            Log.e(PtpConstants.LOG_TAG, "readArrayValue: buffer 长度不足")
            return null
        }
        val count = buffer.int.toLong() and 0xFFFFFFFFL
        val items = mutableListOf<Long>()
        repeat(count.toInt()) {
            val item = readScalar(buffer, elementDt) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "readArrayValue: 元素读取失败, elementDt=0x${elementDt.toString(16)}"
                )
                return null
            }
            items.add(item)
        }
        return PropValue.ArrayValue(items)
    }

    /** 枚举列表：UInt16 个数 + 值序列，元素类型与属性 dataType 一致 */
    private fun readValueList(buffer: ByteBuffer, dataType: Int): List<PropValue>? {
        if (buffer.remaining() < 2) {
            Log.e(PtpConstants.LOG_TAG, "readValueList: buffer 长度不足")
            return null
        }
        val count = buffer.short.toInt() and 0xFFFF
        val values = mutableListOf<PropValue>()
        repeat(count) { idx ->
            val item = readValue(buffer, dataType) ?: run {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "readValueList: readValue fail at index=$idx, dataType=0x${dataType.toString(16)}"
                )
                return null
            }
            values.add(item)
        }
        return values
    }
}
