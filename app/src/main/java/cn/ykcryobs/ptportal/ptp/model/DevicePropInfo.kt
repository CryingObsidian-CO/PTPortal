package cn.ykcryobs.ptportal.ptp.model

enum class IsEnabled(val code: Int) {
    INVALID(0x00),   // 无效/灰色
    VALID(0x01),     // 有效/可操作
    DISPLAY_ONLY(0x02); // 仅显示/不可改

    companion object {
        fun fromCode(code: Int): IsEnabled = entries.find { it.code == code } ?: INVALID
    }
}

// NOTE 当 isEnabled == INVALID 的时候，defaultValue 和 currentValue 应当是不可信的值
data class DevicePropInfo(
    val propertyCode: Int,
    val dataType: Int,
    val isSettable: Boolean,
    val isEnabled: IsEnabled,
    val defaultValue: Long,
    val currentValue: Long,
    val setValues: List<Long>,
    val getSetValues: List<Long>,
)
