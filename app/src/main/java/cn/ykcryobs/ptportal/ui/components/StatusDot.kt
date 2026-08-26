package cn.ykcryobs.ptportal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class StatusLevel {
    Disconnected,
    Connecting,
    Connected,
    Error,
}

@Composable
fun statusColor(level: StatusLevel): Color = when (level) {
    StatusLevel.Disconnected -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    StatusLevel.Connecting -> MaterialTheme.colorScheme.tertiary
    StatusLevel.Connected -> MaterialTheme.colorScheme.secondary
    StatusLevel.Error -> MaterialTheme.colorScheme.error
}

@Composable
fun StatusDot(
    level: StatusLevel,
    size: Dp = 8.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color = statusColor(level), shape = CircleShape),
    )
}
