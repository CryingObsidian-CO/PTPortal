package cn.ykcryobs.ptportal.domain.connection

import kotlinx.coroutines.flow.StateFlow
import cn.ykcryobs.ptportal.ptp.constants.BatteryLevel
import cn.ykcryobs.ptportal.ptp.constants.SLOTStatus

enum class TransportType {
    USB, WiFi,
}

sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data class Connecting(val deviceName: String) : ConnectionState
    data class Connected(
        val transportType: TransportType,
        val deviceInfo: CameraDeviceInfo,
    ) : ConnectionState

    data class Error(val message: String) : ConnectionState
}

data class CameraDeviceInfo(
    val manufacturer: String,
    val model: String,
    val firmwareVersion: String,
    val batteryPercent: Int,
    val batteryLevel: BatteryLevel? = null,
    val slot1Status: SLOTStatus? = null,
    val slot1RemainingPhotoCount: Int = 0,
    val slot1RemainingVideoTimeSec: Int = 0,
    val slot2Status: SLOTStatus? = null,
    val slot2RemainingPhotoCount: Int = 0,
    val slot2RemainingVideoTimeSec: Int = 0,
)

data class DiscoveredDevice(
    val id: String,
    val name: String,
    val model: String,
    val signalStrength: Int,
    val isPaired: Boolean = false,
)

data class PairedDevice(
    val id: String,
    val name: String,
    val model: String,
    val lastConnectedAt: Long,
)

interface CameraConnectionRepository {
    val connectionState: StateFlow<ConnectionState>
    val pairedDevices: StateFlow<List<PairedDevice>>

    fun scanForDevices(onComplete: (List<DiscoveredDevice>) -> Unit, onError: (String) -> Unit)
    fun stopScanning()
    fun connectViaUsb(onSuccess: () -> Unit, onError: (String) -> Unit)
    fun connectViaWifi(
        device: DiscoveredDevice, pin: String, onSuccess: () -> Unit, onError: (String) -> Unit
    )

    fun disconnect()
    fun removePairedDevice(deviceId: String)
}
