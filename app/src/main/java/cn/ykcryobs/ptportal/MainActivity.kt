package cn.ykcryobs.ptportal

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.IntentCompat
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.UsbPtpSession
import cn.ykcryobs.ptportal.ui.theme.PTPortalTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var currentAttachedDevice: UsbDevice? = null
    private val ptpSession = UsbPtpSession()
    private val ACTION_USB_PERMISSION = "cn.ykcryobs.ptportal.USB_PERMISSION"

    private fun isTargetCamera(dev: UsbDevice?): Boolean {
        if (dev == null) return false
        // 检查是否有 PTP 接口
        val hasPtpInterface = (0 until dev.interfaceCount).any { i ->
            val itf = dev.getInterface(i)
            itf.interfaceClass == PtpConstants.USB_CLASS_PTP && itf.interfaceSubclass == PtpConstants.USB_SUBCLASS_PTP
        }
        return dev.vendorId == PtpConstants.SONY_VENDOR_ID && hasPtpInterface
    }

    private val attachReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val dev = IntentCompat.getParcelableExtra(
                        intent, UsbManager.EXTRA_DEVICE, UsbDevice::class.java
                    )
                    if (!isTargetCamera(dev)) {
                        Log.d("PTPortal_PTP", "忽略非目标 USB 设备: $dev")
                        return
                    }
                    ptpSession.close()
                    Log.d("PTPortal_PTP", "检测到设备插入 $dev")
                    currentAttachedDevice = dev
                    dev?.let {
                        requestUsbPermission(it)
                    }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val dev = IntentCompat.getParcelableExtra(
                        intent, UsbManager.EXTRA_DEVICE, UsbDevice::class.java
                    )
                    if (currentAttachedDevice?.deviceId == dev?.deviceId) {
                        ptpSession.close()
                        currentAttachedDevice = null
                        Log.i("PTPortal_PTP", "当前设备已拔出")
                    } else {
                        Log.d("PTPortal_PTP", "非当前设备拔出，忽略")
                    }
                }
            }
        }
    }

    private val usbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_USB_PERMISSION == intent?.action) {

                var dev = IntentCompat.getParcelableExtra(
                    intent, UsbManager.EXTRA_DEVICE, UsbDevice::class.java
                )
                if (dev == null) {
                    dev = currentAttachedDevice
                    Log.w("PTPortal_PTP", "权限广播intent EXTRA_DEVICE为null，回退缓存设备")
                }
                if (dev == null) {
                    Log.e("PTPortal_PTP", "权限回调设备对象为空，直接返回")
                    return
                }

                val usbManager = getSystemService(USB_SERVICE) as UsbManager
                val realGranted = usbManager.hasPermission(dev)
                Log.d(
                    "PTPortal_PTP", "hasPermission=$realGranted , dev=$dev"
                )

                if (realGranted) {
                    Thread {
                        try {
                            val openRet = ptpSession.openDevice(dev)
                            Log.d("PTPortal_PTP", "open result = $openRet")
                            if (!openRet) {
                                Log.e("PTPortal_PTP", "openDevice 打开设备失败")
                                ptpSession.close()
                                return@Thread
                            }

                            val sessionOk = ptpSession.openSession()
                            Log.d("PTPortal_PTP", "PTP 会话建立 = $sessionOk")
                            if (!sessionOk) {
                                Log.e("PTPortal_PTP", "openSession 会话开启失败")
                                ptpSession.close()
                                return@Thread
                            }

                            // SDIO phase1
                            val sdioOk1 = ptpSession.sdioConnect(0x01)
                            Log.d("PTPortal_PTP", "SDIO 连接 (phase = 0x01) $sdioOk1")
                            if (!sdioOk1) {
                                Log.e("PTPortal_PTP", "SDIO phase01握手失败")
                                ptpSession.close()
                                return@Thread
                            }

                            // SDIO phase2
                            val sdioOk2 = ptpSession.sdioConnect(0x02)
                            Log.d("PTPortal_PTP", "SDIO 连接 (phase = 0x02) $sdioOk2")
                            if (!sdioOk2) {
                                Log.e("PTPortal_PTP", "SDIO phase02握手失败")
                                ptpSession.close()
                                return@Thread
                            }

                            // ========== sdioGetExtDeviceInfo ==========
                            val maxRetry = 5
                            var extInfoOk = false
                            var retryCount = 0
                            while (retryCount < maxRetry && !extInfoOk) {
                                retryCount++
                                Log.d(
                                    "PTPortal_PTP",
                                    "sdioGetExtDeviceInfo 第 $retryCount/$maxRetry 次尝试"
                                )
                                extInfoOk = ptpSession.sdioGetExtDeviceInfo(0x0012c)
                                if (!extInfoOk) {
                                    if (retryCount < maxRetry) {
                                        Thread.sleep(100)
                                    }
                                }
                            }

                            if (!extInfoOk) {
                                Log.e(
                                    "PTPortal_PTP",
                                    "sdioGetExtDeviceInfo 超过最大重试次数，获取SDIO扩展信息失败，握手终止"
                                )
                                ptpSession.close()
                                return@Thread
                            }

                            // SDIO phase3
                            val sdioOk3 = ptpSession.sdioConnect(0x03)
                            Log.d("PTPortal_PTP", "SDIO 连接 (phase = 0x03) $sdioOk3")
                            if (!sdioOk3) {
                                Log.e("PTPortal_PTP", "SDIO phase02握手失败")
                                ptpSession.close()
                                return@Thread
                            }

                            // SDIO握手全部阶段完成，再读取设备信息
                            val info = ptpSession.getDeviceInfo()
                            info?.let {
                                Log.d("PTPortal_PTP", "读取到的设备信息: $info")
                            } ?: Log.w("PTPortal_PTP", "getDeviceInfo 返回null")

                            Log.i("PTPortal_PTP", "✅ PTP+SDIO完整初始化全部完成")

                        } catch (ex: Exception) {
                            Log.e("PTPortal_PTP", "PTP打开设备流程发生异常", ex)
                            runCatching { ptpSession.close() }
                        }
                    }.start()
                } else {
                    Log.w("PTPortal_PTP", "无真实USB权限，跳过打开PTP会话")
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PTPortal_APP", "App onCreate 启动成功！！！")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppContextHolder.init(this)

        val attachFilter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(attachReceiver, attachFilter, RECEIVER_NOT_EXPORTED)

        val permFilter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(usbPermissionReceiver, permFilter, RECEIVER_NOT_EXPORTED)

        scanCamera()

        setContent {
            PTPortalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android", modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ptpSession.close()
        unregisterReceiver(attachReceiver)
        unregisterReceiver(usbPermissionReceiver)
    }

    private fun scanCamera() {
        val deviceList = AppContextHolder.usbManager.deviceList
        for ((_, dev) in deviceList) {
            Log.d("PTPortal_PTP", "检测USB设备 vid:${dev.vendorId} pid:${dev.productId}")
            if (isTargetCamera(dev)) {
                Log.i("PTPortal_PTP", "找到相机 pid=${dev.productId}")
                currentAttachedDevice = dev
                requestUsbPermission(dev)
                break
            }
        }
    }

    private fun requestUsbPermission(device: UsbDevice) {
        val intent = Intent(ACTION_USB_PERMISSION).apply {
            setPackage(packageName)
        }
        val permissionIntent = PendingIntent.getBroadcast(
            this, device.deviceId, intent, PendingIntent.FLAG_MUTABLE
        )
        AppContextHolder.usbManager.requestPermission(device, permissionIntent)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PTPortalTheme {
        Greeting("Android")
    }
}