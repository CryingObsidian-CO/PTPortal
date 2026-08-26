package cn.ykcryobs.ptportal.ui.screens.remote

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun LiveViewPlaceholder(
    showGrid: Boolean,
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor),
    ) {
        // TODO: 替换为实际实时画面渲染（SurfaceView / TextureView）
        if (showGrid) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val thirdW = size.width / 3f
                val thirdH = size.height / 3f
                for (i in 1..2) {
                    drawLine(gridColor, Offset(thirdW * i, 0f), Offset(thirdW * i, size.height), strokeWidth = 1.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(gridColor, Offset(0f, thirdH * i), Offset(size.width, thirdH * i), strokeWidth = 1.dp.toPx(), cap = StrokeCap.Round)
                }
            }
        }
    }
}
