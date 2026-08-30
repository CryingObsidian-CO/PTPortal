package cn.ykcryobs.ptportal.ui.screens.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BatteryUnknown
import androidx.compose.material.icons.filled.Battery1Bar
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.Battery5Bar
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.BatteryUnknown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChargingStation
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.ykcryobs.ptportal.R
import cn.ykcryobs.ptportal.domain.connection.CameraDeviceInfo
import cn.ykcryobs.ptportal.domain.connection.ConnectionState
import cn.ykcryobs.ptportal.ptp.constants.BatteryLevel
import cn.ykcryobs.ptportal.ui.components.PTCard
import cn.ykcryobs.ptportal.ui.components.statusColor
import cn.ykcryobs.ptportal.ui.components.statusIcon
import cn.ykcryobs.ptportal.ui.components.StatusLevel

@Composable
fun ConnectionStatusCard(
    state: ConnectionState,
    modifier: Modifier = Modifier,
) {
    PTCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            val (labelRes, level) = when (state) {
                is ConnectionState.Disconnected -> R.string.connection_status_disconnected to StatusLevel.Disconnected

                is ConnectionState.Connecting -> R.string.connection_status_connecting to StatusLevel.Connecting

                is ConnectionState.Connected -> when (state.transportType) {
                    cn.ykcryobs.ptportal.domain.connection.TransportType.USB -> R.string.connection_status_usb_connected to StatusLevel.Connected
                    else -> R.string.connection_status_wifi_connected to StatusLevel.Connected
                }

                is ConnectionState.Error -> R.string.connection_status_error to StatusLevel.Error
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon(level),
                    contentDescription = null,
                    tint = statusColor(level),
                    modifier = Modifier.size(20.dp),
                )
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
            InfoBadge(
                icon = {
                    Icon(
                        batteryIconFor(info.batteryLevel), null, Modifier.height(16.dp)
                    )

                }, text = "${info.batteryPercent}%"
            )
            InfoBadge(
                icon = { Icon(Icons.Filled.SdStorage, null, Modifier.height(16.dp)) },
                text = "%.0f/%.0f GB".format(info.storageFreeGb, info.storageTotalGb)
            )
            InfoBadge(
                icon = { Icon(Icons.Filled.Usb, null, Modifier.height(16.dp)) },
                text = "v${info.firmwareVersion}"
            )
        }
    }
}

private fun batteryIconFor(level: BatteryLevel?): ImageVector = when (level) {
    // 假电池 / 不可用 / 即将耗尽 / 未安装
    BatteryLevel.FAKE_BATTERY -> Icons.Filled.BatteryAlert     // 假电池
    BatteryLevel.UNUSABLE -> Icons.Filled.BatteryAlert         // 不可用（空电）
    BatteryLevel.PRE_END_BATTERY -> Icons.Filled.BatterySaver     // 即将耗尽
    BatteryLevel.BATTERY_NOT_INSTALLED -> Icons.AutoMirrored.Filled.BatteryUnknown   // 未安装 / 未知
    // 电池供电 · 4 格制
    BatteryLevel.BATTERY_LEVEL_1_4 -> Icons.Filled.Battery2Bar      // 1/4
    BatteryLevel.BATTERY_LEVEL_2_4 -> Icons.Filled.Battery4Bar     // 2/4
    BatteryLevel.BATTERY_LEVEL_3_4 -> Icons.Filled.Battery6Bar    // 3/4
    BatteryLevel.BATTERY_LEVEL_4_4 -> Icons.Filled.BatteryFull      // 4/4 满电
    // 电池供电 · 3 格制（与 4 格制是两套，分别用百分比填充图标）
    BatteryLevel.BATTERY_LEVEL_1_3 -> Icons.Filled.Battery2Bar        // 1/3
    BatteryLevel.BATTERY_LEVEL_2_3 -> Icons.Filled.Battery5Bar        // 2/3
    BatteryLevel.BATTERY_LEVEL_3_3 -> Icons.Filled.BatteryFull      // 3/3 满电
    // USB 供给 · 低电预警 + 4 格制
    BatteryLevel.PRE_END_BATTERY_USB_SUPPLY -> Icons.Filled.BatteryChargingFull   // 即将耗尽（USB 供给）
    BatteryLevel.BATTERY_LEVEL_1_4_USB_SUPPLY -> Icons.Filled.BatteryChargingFull  // 1/4（USB）
    BatteryLevel.BATTERY_LEVEL_2_4_USB_SUPPLY -> Icons.Filled.BatteryChargingFull   // 2/4（USB）
    BatteryLevel.BATTERY_LEVEL_3_4_USB_SUPPLY -> Icons.Filled.BatteryChargingFull   // 3/4（USB）
    BatteryLevel.BATTERY_LEVEL_4_4_USB_SUPPLY -> Icons.Filled.BatteryChargingFull // 4/4（USB 满电）
    // USB 总线供电（无电池含义）
    BatteryLevel.USB_BUS_POWER_SUPPLY -> Icons.Filled.ChargingStation      // USB 总线供电
    // 兜底：未知档位 / 无数据
    else -> Icons.AutoMirrored.Filled.BatteryUnknown
}

@Composable
private fun InfoBadge(icon: @Composable () -> Unit, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon()
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
