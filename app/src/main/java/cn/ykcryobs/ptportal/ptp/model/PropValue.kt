package cn.ykcryobs.ptportal.ptp.model

sealed class PropValue {

    data class Scalar(val value: Long) : PropValue() {
        override fun toString(): String = "0x%X".format(value)
    }

    data class Str(val value: String) : PropValue() {
        override fun toString(): String = "\"$value\""
    }

    data class ArrayValue(val items: List<Long>) : PropValue() {
        override fun toString(): String = items.joinToString(prefix = "[", postfix = "]") { "0x%X".format(it) }
    }
}
