package cn.ykcryobs.ptportal.ptp.constants

object PtpConstants {

    const val LOG_TAG = "PTPortal_PTP"
    const val SONY_VENDOR_ID = 0x054C
    const val USB_CLASS_PTP = 6
    const val USB_SUBCLASS_PTP = 1

    const val HEADER_SIZE = 12
    const val RESPONSE_MAX_PARAMS = 4 * 6

    const val USB_TIMEOUT = 5000

    // NOTE 太大会导致尝试读取的时候返回 -1
    const val USB_TRANSFER_BUFFER = 512  // 512
    const val LIVEVIEW_BUFFER = 256 * 1024      // 256KB

}