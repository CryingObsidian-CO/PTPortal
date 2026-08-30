package cn.ykcryobs.ptportal.data.connection

import android.content.Context
import android.util.Log
import android.hardware.usb.UsbDevice
import cn.ykcryobs.ptportal.domain.connection.CameraConnectionRepository
import cn.ykcryobs.ptportal.domain.connection.CameraDeviceInfo
import cn.ykcryobs.ptportal.domain.connection.ConnectionState
import cn.ykcryobs.ptportal.domain.connection.DiscoveredDevice
import cn.ykcryobs.ptportal.domain.connection.PairedDevice
import cn.ykcryobs.ptportal.domain.connection.TransportType
import cn.ykcryobs.ptportal.ptp.PtpSession
import cn.ykcryobs.ptportal.ptp.SdioManager
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.constants.SdioPropCode
import cn.ykcryobs.ptportal.ptp.constants.BatteryLevel
import cn.ykcryobs.ptportal.ptp.model.PropValue
import cn.ykcryobs.ptportal.ptp.parser.DeviceInfoParser
import cn.ykcryobs.ptportal.usb.UsbDeviceDetector
import cn.ykcryobs.ptportal.usb.UsbPermissionHelper
import cn.ykcryobs.ptportal.usb.UsbTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsbCameraConnectionRepository(context: Context) : CameraConnectionRepository {

    private val appContext = context.applicationContext

    private val transport = UsbTransport()
    private val ptpSession = PtpSession(transport)
    private val sdioManager = SdioManager(ptpSession)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var connectJob: Job? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _pairedDevices = MutableStateFlow<List<PairedDevice>>(emptyList())
    override val pairedDevices: StateFlow<List<PairedDevice>> = _pairedDevices

    private val deviceDetector = UsbDeviceDetector(
        onDeviceAttached = { onDeviceAttached(it) },
        onDeviceDetached = { onDeviceDetached(it) },
    )
    private val permissionHelper = UsbPermissionHelper(appContext) { onPermissionGranted(it) }

    /** 在 Activity.onCreate 中调用：注册监听并立即扫描已插入的设备。 */
    fun start() {
        deviceDetector.register(appContext)
        permissionHelper.register()
        deviceDetector.scanAndRequestPermission(permissionHelper)
    }

    /** 在 Activity.onDestroy 中调用：注销监听并释放连接。 */
    fun release() {
        deviceDetector.unregister(appContext)
        permissionHelper.unregister()
        disconnect()
    }

    private fun onDeviceAttached(device: UsbDevice) {
        transport.close()
        permissionHelper.requestUsbPermission(device)
    }

    private fun onDeviceDetached(device: UsbDevice) {
        disconnect()
    }

    private fun onPermissionGranted(device: UsbDevice) {
        connectJob?.cancel()
        connectJob = scope.launch(Dispatchers.IO) {
            try {
                _connectionState.value =
                    ConnectionState.Connecting(device.productName ?: "USB Camera")
                if (!transport.openDevice(device)) {
                    _connectionState.value = ConnectionState.Error("打开设备失败")
                    return@launch
                }
                if (!ptpSession.openSession()) {
                    _connectionState.value = ConnectionState.Error("打开 PTP 会话失败")
                    transport.close()
                    return@launch
                }
                if (!sdioManager.performFullHandshake()) {
                    _connectionState.value = ConnectionState.Error("SDIO 握手失败")
                    ptpSession.closeSession()
                    transport.close()
                    return@launch
                }

                // 读取两块电池属性：0xD20E 电量档位、0xD218 剩余电量（均为 SDIO 0x9251）
                val batteryLevelProp =
                    sdioManager.getExtDevicePropInfo(SdioPropCode.BATTERY_LEVEL.code)
                val batteryRemainingProp =
                    sdioManager.getExtDevicePropInfo(SdioPropCode.BATTERY_REMAINING.code)
                val batteryRemainingRaw =
                    (batteryRemainingProp?.currentValue as? PropValue.Scalar)?.value?.toInt() ?: 0
                val batteryLevel =
                    (batteryLevelProp?.currentValue as? PropValue.Scalar)?.value?.toInt()
                        ?.let { c -> BatteryLevel.entries.firstOrNull { it.code == c.toLong() } }

                val raw = ptpSession.getDeviceInfo()
                val parsed = raw?.let { DeviceInfoParser.parse(it) }
                val info = if (parsed != null) {
                    val (storageFreeGb, storageTotalGb) = readStorageCapacity()
                    CameraDeviceInfo(
                        manufacturer = parsed.manufacturer,
                        model = parsed.model,
                        firmwareVersion = parsed.firmwareVersion,
                        batteryPercent = batteryRemainingRaw,
                        storageFreeGb = storageFreeGb,
                        storageTotalGb = storageTotalGb,

                        batteryLevel = batteryLevel,
                    )
                } else {
                    CameraDeviceInfo("?", "?", "?", 0, 0.0, 0.0)
                }
                _connectionState.value = ConnectionState.Connected(TransportType.USB, info)
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Error(e.message ?: "连接异常")
            }
        }
    }


    private fun readStorageCapacity(): Pair<Double, Double> {
        val ids = ptpSession.getStorageIds()
        if (ids.isNullOrEmpty()) {
            Log.w(PtpConstants.LOG_TAG, "GetStorageIDs 返回空，容量显示 0")
            return 0.0 to 0.0
        }
        var totalBytes = 0L
        var freeBytes = 0L
        for (id in ids) {
            val info = ptpSession.getStorageInfo(id) ?: continue
            totalBytes += info.maxCapacityBytes
            freeBytes += info.freeSpaceBytes
        }
        val bytesToGb = 1.0 / (1024 * 1024 * 1024)
        return freeBytes * bytesToGb to totalBytes * bytesToGb
    }

    override fun connectViaUsb(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // NOTE 真实 USB 连接由“设备插入 + 授权”驱动，这里触发一次重新扫描/授权请求；
        // NOTE 最终结果通过 connectionState 暴露，onSuccess/onError 仅作占位兼容。
        deviceDetector.scanAndRequestPermission(permissionHelper)
    }

    override fun disconnect() {
        connectJob?.cancel()
        ptpSession.closeSession()
        transport.close()
        if (_connectionState.value !is ConnectionState.Disconnected) {
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    override fun scanForDevices(
        onComplete: (List<DiscoveredDevice>) -> Unit,
        onError: (String) -> Unit,
    ) {
        // TODO: 无线（WiFi/PTP-IP）扫描尚未实现。
        onError("无线扫描尚未实现")
    }

    override fun connectViaWifi(
        device: DiscoveredDevice,
        pin: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        // TODO: 无线（WiFi/PTP-IP）连接尚未实现。
        onError("无线连接尚未实现")
    }

    override fun stopScanning() {
        // 真实无线扫描未实现，无需处理。
    }

    override fun removePairedDevice(deviceId: String) {
        _pairedDevices.value = _pairedDevices.value.filterNot { it.id == deviceId }
    }
}
