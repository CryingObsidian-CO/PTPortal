package cn.ykcryobs.ptportal.ui.screens.connection

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.ykcryobs.ptportal.R
import cn.ykcryobs.ptportal.domain.connection.DiscoveredDevice
import cn.ykcryobs.ptportal.domain.connection.ConnectionState
import cn.ykcryobs.ptportal.domain.connection.PairedDevice
import cn.ykcryobs.ptportal.mock.MockCameraConnectionRepository
import cn.ykcryobs.ptportal.ui.components.PTCard
import cn.ykcryobs.ptportal.ui.components.SectionHeader

private enum class ConnectionSubView {
    Main,
    Scan,
    Pairing,
}

@Composable
fun ConnectionScreen(modifier: Modifier = Modifier) {
    val repository = remember { MockCameraConnectionRepository() }
    val viewModel: ConnectionViewModel = viewModel(factory = simpleFactory { ConnectionViewModel(repository) })

    var subView by remember { mutableStateOf(ConnectionSubView.Main) }
    val connectionState by viewModel.connectionState.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val discovered by viewModel.discoveredDevices.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val paired by viewModel.pairedDevices.collectAsState()

    BackHandler(enabled = subView != ConnectionSubView.Main) {
        when (subView) {
            ConnectionSubView.Scan -> {
                viewModel.stopScan()
                subView = ConnectionSubView.Main
            }
            ConnectionSubView.Pairing -> {
                viewModel.clearSelectedDevice()
                subView = ConnectionSubView.Scan
            }
            else -> {}
        }
    }

    AnimatedContent(
        targetState = subView,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = modifier.fillMaxSize(),
    ) { view ->
        when (view) {
            ConnectionSubView.Main -> MainConnectionView(
                state = connectionState,
                pairedDevices = paired,
                onUsbConnect = { viewModel.connectUsb({}, {}) },
                onWirelessClick = {
                    subView = ConnectionSubView.Scan
                    viewModel.startScan()
                },
                onDisconnect = { viewModel.disconnect() },
                onRemovePaired = { viewModel.removePaired(it) },
            )
            ConnectionSubView.Scan -> ScanView(
                isScanning = isScanning,
                devices = discovered,
                onBack = {
                    viewModel.stopScan()
                    subView = ConnectionSubView.Main
                },
                onDeviceSelect = { device ->
                    viewModel.selectForPairing(device)
                    subView = ConnectionSubView.Pairing
                },
                onRetry = { viewModel.startScan() },
            )
            ConnectionSubView.Pairing -> PairingView(
                deviceName = selectedDevice?.name ?: "",
                onBack = {
                    viewModel.clearSelectedDevice()
                    subView = ConnectionSubView.Scan
                },
                onConfirm = { pin ->
                    viewModel.pairWifi(pin, onSuccess = {
                        viewModel.clearSelectedDevice()
                        subView = ConnectionSubView.Main
                    }, onError = {})
                },
            )
        }
    }
}

@Composable
private fun MainConnectionView(
    state: ConnectionState,
    pairedDevices: List<PairedDevice>,
    onUsbConnect: () -> Unit,
    onWirelessClick: () -> Unit,
    onDisconnect: () -> Unit,
    onRemovePaired: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ConnectionStatusCard(state = state)
        }
        if (state is ConnectionState.Disconnected || state is ConnectionState.Error) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onUsbConnect, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Cable, contentDescription = null, Modifier.height(18.dp))
                        Spacer(Modifier.padding(start = 6.dp))
                        Text(stringResource(R.string.connection_action_connect_usb))
                    }
                    OutlinedButton(onClick = onWirelessClick, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Wifi, contentDescription = null, Modifier.height(18.dp))
                        Spacer(Modifier.padding(start = 6.dp))
                        Text(stringResource(R.string.connection_action_wireless))
                    }
                }
            }
        } else if (state is ConnectionState.Connected) {
            item {
                OutlinedButton(onClick = onDisconnect, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.connection_action_disconnect))
                }
            }
        }
        item {
            SectionHeader(title = stringResource(R.string.connection_paired_devices))
        }
        if (pairedDevices.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.connection_no_paired_devices),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(pairedDevices, key = { it.id }) { device ->
                PTCard {
                    PairedDeviceItem(
                        device = device,
                        onReconnect = {},
                        onRemove = { onRemovePaired(device.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanView(
    isScanning: Boolean,
    devices: List<DiscoveredDevice>,
    onBack: () -> Unit,
    onDeviceSelect: (DiscoveredDevice) -> Unit,
    onRetry: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.scan_action_back))
            }
            Text(stringResource(R.string.scan_title), style = MaterialTheme.typography.headlineSmall)
        }
        when {
            isScanning -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.scan_scanning), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            devices.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(stringResource(R.string.scan_empty), textAlign = androidx.compose.ui.text.style.TextAlign.Companion.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onRetry) { Text(stringResource(R.string.connection_action_scan)) }
            }
            else -> {
                Text(
                    text = stringResource(R.string.scan_results, devices.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)) {
                    items(devices, key = { it.id }) { device ->
                        PTCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), onClick = { onDeviceSelect(device) }) {
                            DeviceListItem(device = device, onClick = { onDeviceSelect(device) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PairingView(
    deviceName: String,
    onBack: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var pin by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.scan_action_back))
            }
            Text(stringResource(R.string.pairing_title, deviceName), style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(Modifier.height(16.dp))
        PairingContent(
            device = DiscoveredDevice("", deviceName, "", 0),
            pin = pin,
            onPinChange = { pin = it },
            onPinSubmit = onConfirm,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onConfirm(pin) },
            enabled = pin.length >= 4,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.pairing_action_confirm))
        }
    }
}

private fun <T> simpleFactory(create: () -> T): androidx.lifecycle.ViewModelProvider.Factory =
    object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }
