package cn.ykcryobs.ptportal

import android.content.Context
import android.hardware.usb.UsbManager

object AppContextHolder {
    lateinit var usbManager: UsbManager

    fun init(context: Context) {
        applicationContext = context.applicationContext
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    lateinit var applicationContext: Context
        private set
}
