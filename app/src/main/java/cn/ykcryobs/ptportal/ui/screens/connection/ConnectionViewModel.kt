package cn.ykcryobs.ptportal.ui.screens.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.ykcryobs.ptportal.domain.connection.CameraConnectionRepository
import cn.ykcryobs.ptportal.domain.connection.ConnectionState
import cn.ykcryobs.ptportal.domain.connection.DiscoveredDevice
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ConnectionViewModel(
    private val repository: CameraConnectionRepository,
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = repository.connectionState
    val pairedDevices = repository.pairedDevices

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices

    private val _selectedDevice = MutableStateFlow<DiscoveredDevice?>(null)
    val selectedDevice: StateFlow<DiscoveredDevice?> = _selectedDevice

    private var scanJob: Job? = null

    fun startScan() {
        scanJob?.cancel()
        _isScanning.value = true
        _discoveredDevices.value = emptyList()
        scanJob = viewModelScope.launch {
            delay(500.milliseconds)
            repository.scanForDevices(
                onComplete = { devices ->
                    _discoveredDevices.value = devices
                    _isScanning.value = false
                },
                onError = { _ ->
                    _isScanning.value = false
                },
            )
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        repository.stopScanning()
        _isScanning.value = false
    }

    fun selectForPairing(device: DiscoveredDevice) {
        _selectedDevice.value = device
    }

    fun clearSelectedDevice() {
        _selectedDevice.value = null
    }

    fun connectUsb(onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.connectViaUsb(onSuccess, onError)
    }

    fun pairWifi(pin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val device = _selectedDevice.value ?: return
        repository.connectViaWifi(device, pin, onSuccess, onError)
    }

    fun disconnect() {
        repository.disconnect()
    }

    fun removePaired(deviceId: String) {
        repository.removePairedDevice(deviceId)
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopScanning()
    }
}
