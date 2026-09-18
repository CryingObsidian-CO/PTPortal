package cn.ykcryobs.ptportal.ui.screens.connection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** 连接状态的语义分级：供图标、颜色统一映射。 */
enum class StatusLevel {
    Disconnected, Connecting, Connected, Error,
}

@Composable
fun statusColor(level: StatusLevel): Color = when (level) {
    StatusLevel.Disconnected -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    StatusLevel.Connecting -> MaterialTheme.colorScheme.tertiary
    StatusLevel.Connected -> MaterialTheme.colorScheme.secondary
    StatusLevel.Error -> MaterialTheme.colorScheme.error
}

/** 每个连接状态对应的图标。 */
fun statusIcon(level: StatusLevel): ImageVector = when (level) {
    StatusLevel.Disconnected -> Icons.Filled.LinkOff
    StatusLevel.Connecting -> Icons.Filled.Sync
    StatusLevel.Connected -> Icons.Filled.CheckCircle
    StatusLevel.Error -> Icons.Filled.Error
}