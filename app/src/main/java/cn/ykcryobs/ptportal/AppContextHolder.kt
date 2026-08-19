package cn.ykcryobs.ptportal

import android.content.Context
import android.hardware.usb.UsbManager

object AppContextHolder {
    lateinit var usbManager: UsbManager

    fun init(context: Context) {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    }
}