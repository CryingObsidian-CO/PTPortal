package cn.ykcryobs.ptportal.ui.screens.remote

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.ykcryobs.ptportal.R
import cn.ykcryobs.ptportal.domain.remote.CameraSettings
import cn.ykcryobs.ptportal.domain.remote.FocusMode
import cn.ykcryobs.ptportal.domain.remote.ShootingMode
import cn.ykcryobs.ptportal.domain.remote.WhiteBalanceMode
import cn.ykcryobs.ptportal.ui.components.ParamChip

@Composable
fun ParameterPanel(
    settings: CameraSettings,
    onApertureChange: (Float) -> Unit,
    onShutterChange: (String) -> Unit,
    onIsoChange: (Int) -> Unit,
    onExposureCompensation: (Float) -> Unit,
    onWhiteBalanceChange: (WhiteBalanceMode) -> Unit,
    onShootingModeChange: (ShootingMode) -> Unit,
    onFocusModeChange: (FocusMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.remote_params_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        // Aperture
        ChipRow(
            label = "F",
            options = settings.apertureOptions,
            selectedIndex = settings.apertureOptions.indexOf(settings.aperture),
            format = { "%.1f".format(it) },
            onSelect = { onApertureChange(it) },
        )
        Spacer(Modifier.height(6.dp))

        // Shutter speed
        ChipRow(
            label = stringResource(R.string.remote_param_shutter),
            options = settings.shutterOptions,
            selectedIndex = settings.shutterOptions.indexOf(settings.shutterSpeed),
            format = { it },
            onSelect = { onShutterChange(it) },
        )
        Spacer(Modifier.height(6.dp))

        // ISO
        ChipRow(
            label = "ISO",
            options = settings.isoOptions,
            selectedIndex = settings.isoOptions.indexOf(settings.iso),
            format = { it.toString() },
            onSelect = { onIsoChange(it) },
        )
        Spacer(Modifier.height(12.dp))

        // Exposure compensation slider
        Text(
            text = "${stringResource(R.string.remote_param_exp_comp)} ${if (settings.exposureCompensation >= 0) "+" else ""}${"%.1f".format(settings.exposureCompensation)} EV",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Slider(
            value = settings.exposureCompensation,
            onValueChange = { newValue -> onExposureCompensation(Math.round(newValue * 3f) / 3f) },
            valueRange = -3f..3f,
            steps = 17, // 0.5 EV steps from -3 to +3 => 6*2-1 = 11 intervals but 0.5 steps means 12 positions minus endpoints
        )
        Spacer(Modifier.height(8.dp))

        // Shooting mode selector
        Text(stringResource(R.string.remote_param_mode), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(top = 4.dp),
        ) {
            ShootingMode.entries.forEach { mode ->
                ParamChip(
                    label = shootingModeLabel(mode),
                    value = "",
                    isSelected = settings.shootingMode == mode,
                    onClick = { onShootingModeChange(mode) },
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        // White balance selector
        Text(stringResource(R.string.remote_param_wb), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(top = 4.dp),
        ) {
            WhiteBalanceMode.entries.forEach { wb ->
                ParamChip(
                    label = whiteBalanceLabel(wb),
                    value = "",
                    isSelected = settings.whiteBalance == wb,
                    onClick = { onWhiteBalanceChange(wb) },
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        // Focus mode toggle
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FocusMode.entries.forEach { fm ->
                ParamChip(
                    label = if (fm == FocusMode.AF) "AF" else "MF",
                    value = "",
                    isSelected = settings.focusMode == fm,
                    onClick = { onFocusModeChange(fm) },
                )
            }
        }
    }
}

@Composable
private fun <T> ChipRow(
    label: String,
    options: List<T>,
    selectedIndex: Int,
    format: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp).widthIn(min = 32.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            options.forEachIndexed { index, option ->
                ParamChip(
                    label = format(option),
                    value = "",
                    isSelected = index == selectedIndex,
                onClick = { onSelect(option) },
                )
            }
        }
    }
}

@Composable
private fun shootingModeLabel(mode: ShootingMode): String = when (mode) {
    ShootingMode.SingleShot -> stringResource(R.string.remote_mode_single)
    ShootingMode.Continuous -> stringResource(R.string.remote_mode_continuous)
    ShootingMode.Timer -> stringResource(R.string.remote_mode_timer)
    ShootingMode.Video -> stringResource(R.string.remote_mode_video)
}

@Composable
private fun whiteBalanceLabel(wb: WhiteBalanceMode): String = when (wb) {
    WhiteBalanceMode.Auto -> stringResource(R.string.remote_wb_auto)
    WhiteBalanceMode.Daylight -> stringResource(R.string.remote_wb_daylight)
    WhiteBalanceMode.Shade -> stringResource(R.string.remote_wb_shade)
    WhiteBalanceMode.Cloudy -> stringResource(R.string.remote_wb_cloudy)
    WhiteBalanceMode.Tungsten -> stringResource(R.string.remote_wb_tungsten)
    WhiteBalanceMode.Fluorescent -> stringResource(R.string.remote_wb_fluorescent)
    WhiteBalanceMode.Flash -> stringResource(R.string.remote_wb_flash)
    WhiteBalanceMode.Custom -> stringResource(R.string.remote_wb_custom)
}
