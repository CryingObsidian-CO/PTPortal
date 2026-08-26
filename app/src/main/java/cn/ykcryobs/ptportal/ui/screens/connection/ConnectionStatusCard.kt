package cn.ykcryobs.ptportal.ui.screens.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.ykcryobs.ptportal.R
import cn.ykcryobs.ptportal.domain.connection.CameraDeviceInfo
import cn.ykcryobs.ptportal.domain.connection.ConnectionState
import cn.ykcryobs.ptportal.ui.components.PTCard
import cn.ykcryobs.ptportal.ui.components.StatusDot
import cn.ykcryobs.ptportal.ui.components.StatusLevel

@Composable
fun ConnectionStatusCard(
    state: ConnectionState,
    modifier: Modifier = Modifier,
) {
    PTCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            val (labelRes, level) = when (state) {
                is ConnectionState.Disconnected ->
                    R.string.connection_status_disconnected to StatusLevel.Disconnected
                is ConnectionState.Connecting ->
                    R.string.connection_status_connecting to StatusLevel.Connecting
                is ConnectionState.Connected -> when (state.transportType) {
                    cn.ykcryobs.ptportal.domain.connection.TransportType.USB -> R.string.connection_status_usb_connected to StatusLevel.Connected
                    else -> R.string.connection_status_wifi_connected to StatusLevel.Connected
                }
                is ConnectionState.Error ->
                    R.string.connection_status_error to StatusLevel.Error
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(level = level)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            if (state is ConnectionState.Connected) {
                Spacer(Modifier.height(12.dp))
                DeviceInfoGrid(state.deviceInfo)
            } else if (state is ConnectionState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun DeviceInfoGrid(info: CameraDeviceInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "${info.manufacturer} ${info.model}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoBadge(icon = { Icon(Icons.Filled.BatteryFull, null, Modifier.height(16.dp)) }, text = "${info.batteryPercent}%")
            InfoBadge(icon = { Icon(Icons.Filled.SdStorage, null, Modifier.height(16.dp)) }, text = "%.0f/%.0f GB".format(info.storageFreeGb, info.storageTotalGb))
            InfoBadge(icon = { Icon(Icons.Filled.Usb, null, Modifier.height(16.dp)) }, text = "v${info.firmwareVersion}")
        }
    }
}

@Composable
private fun InfoBadge(icon: @Composable () -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        icon()
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
