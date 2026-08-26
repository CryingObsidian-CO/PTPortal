package cn.ykcryobs.ptportal.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PTCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val shape = MaterialTheme.shapes.medium

    if (onClick != null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            content = content,
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            content = content,
        )
    }
}
