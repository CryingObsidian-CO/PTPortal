package cn.ykcryobs.ptportal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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

@Composable
fun StatusDot(
    level: StatusLevel,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color = statusColor(level), shape = CircleShape),
    )
}
