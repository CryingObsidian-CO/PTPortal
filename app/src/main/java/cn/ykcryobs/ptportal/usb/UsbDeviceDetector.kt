package cn.ykcryobs.ptportal.usb

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

class UsbDeviceDetector(
    private val onDeviceAttached: (UsbDevice) -> Unit,
    private val onDeviceDetached: (UsbDevice) -> Unit
) {

    private var currentAttachedDevice: UsbDevice? = null

    private val attachReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val dev = IntentCompat.getParcelableExtra(
                        intent, UsbManager.EXTRA_DEVICE, UsbDevice::class.java
                    )
                    if (!isTargetCamera(dev)) {
                        Log.d(PtpConstants.LOG_TAG, "忽略非目标 USB 设备: $dev")
                        return
                    }
                    Log.d(PtpConstants.LOG_TAG, "检测到设备插入 $dev")
                    currentAttachedDevice = dev
                    dev?.let { onDeviceAttached(it) }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val dev = IntentCompat.getParcelableExtra(
                        intent, UsbManager.EXTRA_DEVICE, UsbDevice::class.java
                    )
                    if (currentAttachedDevice?.deviceId == dev?.deviceId) {
                        currentAttachedDevice = null
                        dev?.let { onDeviceDetached(it) }
                        Log.i(PtpConstants.LOG_TAG, "当前设备已拔出")
                    } else {
                        Log.d(PtpConstants.LOG_TAG, "非当前设备拔出，忽略")
                    }
                }
            }
        }
    }

    fun isTargetCamera(dev: UsbDevice?): Boolean {
        if (dev == null) return false
        val hasPtpInterface = (0 until dev.interfaceCount).any { i ->
            val itf = dev.getInterface(i)
            itf.interfaceClass == PtpConstants.USB_CLASS_PTP && itf.interfaceSubclass == PtpConstants.USB_SUBCLASS_PTP
        }
        return dev.vendorId == PtpConstants.SONY_VENDOR_ID && hasPtpInterface
    }

    fun scanAndRequestPermission(permissionHelper: UsbPermissionHelper) {
        val deviceList = AppContextHolder.usbManager.deviceList
        for ((_, dev) in deviceList) {
            Log.d(PtpConstants.LOG_TAG, "检测USB设备 vid:${dev.vendorId} pid:${dev.productId}")
            if (isTargetCamera(dev)) {
                Log.i(PtpConstants.LOG_TAG, "找到相机 pid=${dev.productId}")
                currentAttachedDevice = dev
                permissionHelper.requestUsbPermission(dev)
                break
            }
        }
    }

    fun register(context: Context) {
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        context.registerReceiver(attachReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(attachReceiver)
    }
}
