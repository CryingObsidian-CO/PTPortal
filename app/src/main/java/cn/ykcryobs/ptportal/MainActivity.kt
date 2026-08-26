package cn.ykcryobs.ptportal

import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.ykcryobs.ptportal.ptp.PtpSession
import cn.ykcryobs.ptportal.ptp.SdioManager
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ui.navigation.PTScaffold
import cn.ykcryobs.ptportal.ui.theme.PTPortalTheme
import cn.ykcryobs.ptportal.usb.UsbDeviceDetector
import cn.ykcryobs.ptportal.usb.UsbPermissionHelper
import cn.ykcryobs.ptportal.usb.UsbTransport
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {

    private val transport = UsbTransport()
    private val ptpSession = PtpSession(transport)
    private val sdioManager = SdioManager(ptpSession)

    private lateinit var deviceDetector: UsbDeviceDetector
    private lateinit var permissionHelper: UsbPermissionHelper

    private val initializing = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(PtpConstants.LOG_TAG, "App onCreate 启动成功！！！")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppContextHolder.init(this)

        permissionHelper = UsbPermissionHelper(this) { dev -> onPermissionGranted(dev) }
        deviceDetector = UsbDeviceDetector(
            onDeviceAttached = { dev -> onDeviceAttached(dev) },
            onDeviceDetached = { dev -> onDeviceDetached(dev) })

        deviceDetector.register(this)
        permissionHelper.register()

        deviceDetector.scanAndRequestPermission(permissionHelper)

        setContent {
            PTPortalTheme {
                PTScaffold()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ptpSession.closeSession()
        transport.close()
        deviceDetector.unregister(this)
        permissionHelper.unregister()
    }

    private fun onDeviceAttached(device: UsbDevice) {
        transport.close()
        permissionHelper.requestUsbPermission(device)
    }

    private fun onDeviceDetached(device: UsbDevice) {
        transport.close()
    }

    private fun onPermissionGranted(device: UsbDevice) {
        if (!initializing.compareAndSet(false, true)) return
        Thread {
            try {
                val openRet = transport.openDevice(device)
                Log.d(PtpConstants.LOG_TAG, "open result = $openRet")
                if (!openRet) {
                    Log.e(PtpConstants.LOG_TAG, "openDevice 打开设备失败")
                    transport.close()
                    return@Thread
                }

                val sessionOk = ptpSession.openSession()
                Log.d(PtpConstants.LOG_TAG, "PTP 会话建立 = $sessionOk")
                if (!sessionOk) {
                    Log.e(PtpConstants.LOG_TAG, "openSession 会话开启失败")
                    transport.close()
                    return@Thread
                }

                val handshakeOk = sdioManager.performFullHandshake()
                if (!handshakeOk) {
                    Log.e(PtpConstants.LOG_TAG, "SDIO 握手失败")
                    ptpSession.closeSession()
                    transport.close()
                    return@Thread
                }

                val info = ptpSession.getDeviceInfo()
                info?.let {
                    Log.d(PtpConstants.LOG_TAG, "成功读取到的设备信息")
                } ?: Log.w(PtpConstants.LOG_TAG, "getDeviceInfo 返回null")

            } catch (ex: Exception) {
                Log.e(PtpConstants.LOG_TAG, "PTP打开设备流程发生异常", ex)
                runCatching { ptpSession.closeSession() }
                runCatching { transport.close() }
            } finally {
                initializing.set(false)
            }
        }.start()
    }
}
