package cn.ykcryobs.ptportal.ui.screens.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.ykcryobs.ptportal.R
import cn.ykcryobs.ptportal.domain.remote.RecordingState
import cn.ykcryobs.ptportal.mock.MockCameraControlRepository

@Composable
fun RemoteScreen(modifier: Modifier = Modifier) {
    val repository = remember { MockCameraControlRepository() }
    val viewModel: RemoteViewModel =
        viewModel(factory = simpleFactory { RemoteViewModel(repository) })

    val settings by viewModel.settings.collectAsState()
    val recording by viewModel.recordingState.collectAsState()


    var showGrid by remember { mutableStateOf(true) }

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = modifier.fillMaxSize()) {
        if (isLandscape) {
            Row(modifier = modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight(),
                ) {
                    LiveViewPlaceholder(showGrid = showGrid, modifier = Modifier.fillMaxSize())
                    LiveViewControls(
                        showGrid = showGrid,
                        onToggleGrid = { showGrid = !showGrid },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(16.dp))
                    ParameterPanel(
                        settings = settings,
                        onApertureChange = viewModel::setAperture,
                        onShutterChange = viewModel::setShutterSpeed,
                        onIsoChange = viewModel::setIso,
                        onExposureCompensation = viewModel::setExposureCompensation,
                        onWhiteBalanceChange = viewModel::setWhiteBalance,
                        onShootingModeChange = viewModel::setShootingMode,
                        onFocusModeChange = viewModel::setFocusMode,
                    )
                    Spacer(Modifier.height(16.dp))
                    ShutterRow(
                        recording = recording,
                        onCapture = { viewModel.capture() },
                        onRecordToggle = { viewModel.toggleRecording() })
                    Spacer(Modifier.height(24.dp))
                }
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                ) {
                    LiveViewPlaceholder(showGrid = showGrid, modifier = Modifier.fillMaxSize())
                    LiveViewControls(
                        showGrid = showGrid,
                        onToggleGrid = { showGrid = !showGrid },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                ShutterRow(
                    recording = recording,
                    onCapture = { viewModel.capture() },
                    onRecordToggle = { viewModel.toggleRecording() })
                Spacer(Modifier.height(12.dp))
                ParameterPanel(
                    settings = settings,
                    onApertureChange = viewModel::setAperture,
                    onShutterChange = viewModel::setShutterSpeed,
                    onIsoChange = viewModel::setIso,
                    onExposureCompensation = viewModel::setExposureCompensation,
                    onWhiteBalanceChange = viewModel::setWhiteBalance,
                    onShootingModeChange = viewModel::setShootingMode,
                    onFocusModeChange = viewModel::setFocusMode,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LiveViewControls(
    showGrid: Boolean,
    onToggleGrid: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        IconButton(onClick = onToggleGrid, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.GridOn,
                contentDescription = stringResource(R.string.remote_grid_toggle),
                tint = if (showGrid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.5f
                ),
            )
        }
    }
}

@Composable
private fun ShutterRow(
    recording: RecordingState,
    onCapture: () -> Unit,
    onRecordToggle: () -> Unit,
) {
    val isRecordingNow = recording is RecordingState.Recording

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "22222",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(2f)
        ) {
            IconButton(onClick = onRecordToggle, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Filled.FiberManualRecord,
                    contentDescription = stringResource(R.string.remote_record),
                    tint = if (isRecordingNow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.5f
                    ),
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.width(24.dp))

            ShutterButton(onClick = onCapture, size = 60.dp)
            Spacer(Modifier.width(24.dp))
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (recording is RecordingState.Recording) {
                Text(
                    text = formatDuration(recording.elapsedSeconds),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            Text(
                text = "22222",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun <T> simpleFactory(create: () -> T): androidx.lifecycle.ViewModelProvider.Factory =
    object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM =
            create() as VM
    }
