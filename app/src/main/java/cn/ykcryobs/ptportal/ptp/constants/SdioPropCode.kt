package cn.ykcryobs.ptportal.ptp.constants

import cn.ykcryobs.ptportal.ptp.model.PropValue
import cn.ykcryobs.ptportal.ptp.model.EnumEntry
import kotlin.reflect.KClass

/** 值标注器 */
sealed interface ValueLabeler {
    fun label(value: Long): String

    /** 原样 hex，默认 */
    object Raw : ValueLabeler {
        override fun label(value: Long) = "0x%X".format(value)
    }

    /** 连续值换算：raw / divisor + 单位，如 Scaled(100, 1, "mm") → 1540 = "15.4mm" */
    data class Scaled(val divisor: Long, val decimals: Int = 0, val unit: String = "") :
        ValueLabeler {
        override fun label(value: Long): String =
            "%.${decimals}f${unit}".format(value.toDouble() / divisor)
    }

    /**
     * 基于 EnumEntry 枚举类的标注器
     */
    class Enum<E : EnumEntry>(enumClass: KClass<E>) : ValueLabeler {
        private val codeToLabel: Map<Long, String> =
            enumClass.java.enumConstants!!.associate { it.code to it.label }
        private val labelToCode: Map<String, Long> =
            enumClass.java.enumConstants!!.associate { it.label to it.code }

        override fun label(value: Long): String = codeToLabel[value] ?: "0x%X".format(value)
        fun codeOf(label: String): Long? = labelToCode[label]
        val entries: List<E> = enumClass.java.enumConstants!!.toList()
    }

    /** 任意规则，lambda 兜底 */
    class Formatted(val format: (Long) -> String) : ValueLabeler {
        override fun label(value: Long) = format(value)
    }
}

sealed interface SdioProp {
    val code: Int
    val description: String

    data class Unknown(val rawCode: Int) : SdioProp {
        override val code: Int get() = rawCode
        override val description: String get() = "Unknown"
        override fun toString(): String = "Unknown(0x%04X)".format(rawCode)
    }

    fun labelOf(value: PropValue): String {
        val labeler = (this as? SdioPropCode)?.labeler
        return when (value) {
            is PropValue.Scalar -> labeler?.label(value.value) ?: "0x%X".format(value.value)
            is PropValue.ArrayValue -> value.items.joinToString(prefix = "[", postfix = "]") {
                labeler?.label(it) ?: "0x%X".format(it)
            }

            is PropValue.Str -> value.toString()
        }
    }
}

enum class SdioPropCode(
    override val code: Int,
    override val description: String,
    val labeler: ValueLabeler = ValueLabeler.Raw
) : SdioProp {
    WHITE_BALANCE(0x5005, "White Balance", ValueLabeler.Enum(WhiteBalance::class)),
    F_NUMBER(0x5007, "F-Number", ValueLabeler.Enum(FNumber::class)),
    FOCUS_MODE(0x500A, "Focus Mode", ValueLabeler.Enum(FocusMode::class)),
    METERING_MODE(0x500B, "Metering Mode", ValueLabeler.Enum(MeteringMode::class)),
    FLASH_MODE(0x500C, "Flash Mode", ValueLabeler.Enum(FlashMode::class)),
    EXPOSURE_MODE(0x500E, "Exposure Mode", ValueLabeler.Enum(ExposureMode::class)),
    EXPOSURE_BIAS(0x5010, "Exposure Bias", ValueLabeler.Scaled(1000, 1, "EV")),
    STILL_CAPTURE_MODE(0x5013, "Still Capture Mode", ValueLabeler.Enum(StillCaptureMode::class)),
    IRIS_MODE(0xD001, "Iris Mode", ValueLabeler.Enum(IrisMode::class)),
    FOCAL_DISTANCE(0xD004, "Focal Distance in Meter", ValueLabeler.Raw);
    // TODO 等待补全

    override fun toString(): String = "${description}(0x%04X)".format(code)

    companion object {
        fun fromCode(code: Int): SdioProp =
            entries.find { it.code == code } ?: SdioProp.Unknown(code)
    }
}
