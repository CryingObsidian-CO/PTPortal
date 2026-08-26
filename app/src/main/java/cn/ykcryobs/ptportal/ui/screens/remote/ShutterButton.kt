package cn.ykcryobs.ptportal.ui.screens.remote

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShutterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(100),
        label = "shutter_scale",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size + 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .padding(5.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        )
    }
}
