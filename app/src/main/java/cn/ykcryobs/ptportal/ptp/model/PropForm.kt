package cn.ykcryobs.ptportal.ptp.model

sealed class PropForm {

    /** formFlag=0x01：连续区间 min..max，步进 step */
    data class Range(val min: Long, val max: Long, val step: Long) : PropForm()

    /** formFlag=0x02：枚举，setOnly 为仅可设列表，getSet 为可设可读列表 */
    data class EnumSet(val setOnly: List<PropValue>, val getSet: List<PropValue>) : PropForm()

    /** formFlag=其他：无形式约束 */
    object None : PropForm() {
        override fun toString(): String = "None"
    }
}
