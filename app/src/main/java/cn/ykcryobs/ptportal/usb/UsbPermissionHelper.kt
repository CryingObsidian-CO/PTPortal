package cn.ykcryobs.ptportal.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.IntentCompat
import cn.ykcryobs.ptportal.AppContextHolder
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants

class UsbPermissionHelper(
    private val context: Context, private val onPermissionGranted: (UsbDevice) -> Unit
) {

    companion object {
        private const val ACTION_USB_PERMISSION = "cn.ykcryobs.ptportal.USB_PERMISSION"
    }

    private var cachedDevice: UsbDevice? = null

    private val usbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (ACTION_USB_PERMISSION == intent?.action) {
                var dev = IntentCompat.getParcelableExtra(
                    intent, UsbManager.EXTRA_DEVICE, UsbDevice::class.java
                )
                if (dev == null) {
                    dev = cachedDevice
                    Log.w(PtpConstants.LOG_TAG, "权限广播intent EXTRA_DEVICE为null，回退缓存设备")
                }
                if (dev == null) {
                    Log.e(PtpConstants.LOG_TAG, "权限回调设备对象为空，直接返回")
                    return
                }

                val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
                val realGranted = usbManager.hasPermission(dev)
                Log.d(PtpConstants.LOG_TAG, "hasPermission=$realGranted , dev=$dev")

                if (realGranted) {
                    onPermissionGranted(dev)
                } else {
                    Log.w(PtpConstants.LOG_TAG, "无真实USB权限，跳过打开PTP会话")
                }
            }
        }
    }

    fun requestUsbPermission(device: UsbDevice) {
        cachedDevice = device
        val intent = Intent(ACTION_USB_PERMISSION).apply {
            setPackage(context.packageName)
        }
        val permissionIntent = PendingIntent.getBroadcast(
            context, device.deviceId, intent, PendingIntent.FLAG_MUTABLE
        )
        AppContextHolder.usbManager.requestPermission(device, permissionIntent)
    }

    fun register() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    fun unregister() {
        context.unregisterReceiver(usbPermissionReceiver)
    }
}
