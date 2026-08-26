package cn.ykcryobs.ptportal.ui.screens.settings

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.ykcryobs.ptportal.R
import cn.ykcryobs.ptportal.domain.preferences.AppLanguage
import cn.ykcryobs.ptportal.domain.preferences.DefaultConnectionMode
import cn.ykcryobs.ptportal.domain.preferences.ThemeMode
import cn.ykcryobs.ptportal.domain.preferences.TransferQuality
import cn.ykcryobs.ptportal.domain.preferences.UserPreferences
import cn.ykcryobs.ptportal.domain.preferences.UserPreferencesRepository
import cn.ykcryobs.ptportal.ui.components.PTCard
import cn.ykcryobs.ptportal.ui.components.SectionHeader

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreen(
    repository: UserPreferencesRepository,
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel = viewModel(
        factory = simpleFactory { SettingsViewModel(repository) },
    )
    val preferences by viewModel.preferences.collectAsState()
    var showLicenses by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SectionHeader(title = stringResource(R.string.settings_group_connection))
        }
        item {
            PTCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ChoiceSelector(
                        title = stringResource(R.string.settings_default_connection),
                        selectedIndex = preferences.defaultConnectionMode.ordinal,
                        options = listOf(
                            stringResource(R.string.settings_connection_auto),
                            stringResource(R.string.settings_connection_usb),
                            stringResource(R.string.settings_connection_wifi),
                        ),
                        onSelect = { index ->
                            viewModel.setDefaultConnectionMode(DefaultConnectionMode.entries[index])
                        },
                    )
                    SwitchRow(
                        title = stringResource(R.string.settings_auto_reconnect),
                        subtitle = stringResource(R.string.settings_auto_reconnect_hint),
                        checked = preferences.autoReconnect,
                        onChange = viewModel::setAutoReconnect,
                    )
                    SliderRow(
                        title = stringResource(R.string.settings_scan_interval),
                        valueText = stringResource(
                            R.string.settings_scan_interval_value,
                            preferences.scanIntervalSeconds,
                        ),
                        value = preferences.scanIntervalSeconds.toFloat(),
                        onValueChange = { value -> viewModel.setScanIntervalSeconds(value.toInt()) },
                    )
                }
            }
        }

        item {
            SectionHeader(title = stringResource(R.string.settings_group_capture))
        }
        item {
            PTCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    InfoRow(
                        title = stringResource(R.string.settings_save_path),
                        detail = UserPreferences.DEFAULT_SAVE_PATH_HINT,
                    )
                    ChoiceSelector(
                        title = stringResource(R.string.settings_transfer_quality),
                        selectedIndex = preferences.transferQuality.ordinal,
                        options = listOf(
                            stringResource(R.string.settings_quality_standard),
                            stringResource(R.string.settings_quality_high),
                            stringResource(R.string.settings_quality_original),
                        ),
                        onSelect = { index ->
                            viewModel.setTransferQuality(TransferQuality.entries[index])
                        },
                    )
                }
            }
        }

        item {
            SectionHeader(title = stringResource(R.string.settings_group_appearance))
        }
        item {
            PTCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ChoiceSelector(
                        title = stringResource(R.string.settings_theme_mode),
                        selectedIndex = preferences.themeMode.ordinal,
                        options = listOf(
                            stringResource(R.string.settings_theme_system),
                            stringResource(R.string.settings_theme_light),
                            stringResource(R.string.settings_theme_dark),
                        ),
                        onSelect = { index ->
                            viewModel.setThemeMode(ThemeMode.entries[index])
                        },
                    )
                    val context = LocalContext.current
                    ChoiceSelector(
                        title = stringResource(R.string.settings_language),
                        selectedIndex = preferences.language.ordinal,
                        options = listOf(
                            stringResource(R.string.settings_language_system),
                            stringResource(R.string.settings_language_chinese),
                            stringResource(R.string.settings_language_english),
                        ),
                        onSelect = { index ->
                            val language = AppLanguage.entries[index]
                            viewModel.setLanguage(language)
                            applyAppLanguage(context, language)
                        },
                    )
                }
            }
        }

        item {
            SectionHeader(title = stringResource(R.string.settings_group_about))
        }
        item {
            val versionName = LocalContext.current.versionName()
            PTCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InfoRow(
                        title = stringResource(R.string.settings_version_info),
                        detail = versionName,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(R.string.settings_open_licenses))
                        TextButton(onClick = { showLicenses = true }) {
                            Text(stringResource(R.string.settings_action_view))
                        }
                    }
                }
            }
        }
    }

    if (showLicenses) {
        LicensesDialog(onDismissRequest = { showLicenses = false })
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title)
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SliderRow(
    title: String,
    valueText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title)
            Text(valueText, fontWeight = FontWeight.Medium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 2f..10f,
            steps = 7,
        )
    }
}

@Composable
private fun ChoiceSelector(
    title: String,
    selectedIndex: Int,
    options: List<String>,
    onSelect: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    title: String,
    detail: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title)
        Text(
            text = detail,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LicensesDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.settings_licenses_title)) },
        text = { Text(stringResource(R.string.settings_licenses_content)) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.settings_action_close))
            }
        },
    )
}

private fun Context.versionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName ?: "--"
}

private fun applyAppLanguage(context: Context, language: AppLanguage) {
    val localeManager = context.getSystemService(LocaleManager::class.java) ?: return
    localeManager.applicationLocales = when (language) {
        AppLanguage.System -> LocaleList.getEmptyLocaleList()
        AppLanguage.Chinese -> LocaleList.forLanguageTags("zh-CN")
        AppLanguage.English -> LocaleList.forLanguageTags("en")
    }
}

private fun <T : ViewModel> simpleFactory(create: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }
