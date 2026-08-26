package cn.ykcryobs.ptportal.mock

import cn.ykcryobs.ptportal.domain.connection.CameraConnectionRepository
import cn.ykcryobs.ptportal.domain.connection.CameraDeviceInfo
import cn.ykcryobs.ptportal.domain.connection.ConnectionState
import cn.ykcryobs.ptportal.domain.connection.DiscoveredDevice
import cn.ykcryobs.ptportal.domain.connection.PairedDevice
import cn.ykcryobs.ptportal.domain.connection.TransportType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MockCameraConnectionRepository : CameraConnectionRepository {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var scanJob: Job? = null
    private var connectJob: Job? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _pairedDevices = MutableStateFlow(
        listOf(
            PairedDevice("mock-1", "ILCE-7M4", "Sony Alpha 7 IV", System.currentTimeMillis() - 86_400_000),
            PairedDevice("mock-2", "ZV-E10", "Sony ZV-E10", System.currentTimeMillis() - 7 * 86_400_000),
        )
    )
    override val pairedDevices: StateFlow<List<PairedDevice>> = _pairedDevices

    private val mockDiscovered = listOf(
        DiscoveredDevice("w-1", "ILCE-7CR", "Alpha 7CR", 3),
        DiscoveredDevice("w-2", "ILCE-6700", "Alpha 6700", 2),
        DiscoveredDevice("w-3", "ZV-1 II", "ZV-1 II", 1),
        DiscoveredDevice("w-4", "FX30", "FX30 Cinema Line", 4),
    )

    override fun scanForDevices(onComplete: (List<DiscoveredDevice>) -> Unit, onError: (String) -> Unit) {
        scanJob?.cancel()
        scanJob = scope.launch {
            delay(2000)
            onComplete(mockDiscovered.shuffled().take(Random.nextInt(2, mockDiscovered.size + 1)))
        }
    }

    override fun stopScanning() {
        scanJob?.cancel()
        scanJob = null
    }

    override fun connectViaUsb(onSuccess: () -> Unit, onError: (String) -> Unit) {
        connectJob?.cancel()
        _connectionState.value = ConnectionState.Connecting("USB Camera")
        connectJob = scope.launch {
            delay(1500)
            if (Random.nextBoolean()) {
                val info = CameraDeviceInfo(
                    manufacturer = "Sony",
                    model = "ILCE-7M4",
                    firmwareVersion = "3.01",
                    batteryPercent = Random.nextInt(20, 100),
                    storageFreeGb = Random.nextDouble(20.0, 200.0),
                    storageTotalGb = 256.0,
                )
                _connectionState.value = ConnectionState.Connected(TransportType.USB, info)
                onSuccess()
            } else {
                _connectionState.value = ConnectionState.Error("USB device not found")
                onError("USB device not found")
            }
        }
    }

    override fun connectViaWifi(device: DiscoveredDevice, pin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        connectJob?.cancel()
        _connectionState.value = ConnectionState.Connecting(device.name)
        connectJob = scope.launch {
            delay(2000)
            if (pin.length >= 4) {
                val info = CameraDeviceInfo(
                    manufacturer = "Sony",
                    model = device.model,
                    firmwareVersion = "2.10",
                    batteryPercent = Random.nextInt(15, 100),
                    storageFreeGb = Random.nextDouble(10.0, 180.0),
                    storageTotalGb = 128.0,
                )
                _connectionState.value = ConnectionState.Connected(TransportType.WiFi, info)
                onSuccess()
            } else {
                _connectionState.value = ConnectionState.Error("Invalid PIN code")
                onError("Invalid PIN code")
            }
        }
    }

    override fun disconnect() {
        connectJob?.cancel()
        _connectionState.value = ConnectionState.Disconnected
    }

    override fun removePairedDevice(deviceId: String) {
        _pairedDevices.value = _pairedDevices.value.filterNot { it.id == deviceId }
    }
}
