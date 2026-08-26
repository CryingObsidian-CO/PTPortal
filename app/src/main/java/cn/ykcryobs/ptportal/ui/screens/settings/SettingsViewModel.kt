package cn.ykcryobs.ptportal.ui.screens.settings

import androidx.lifecycle.ViewModel
import cn.ykcryobs.ptportal.domain.preferences.AppLanguage
import cn.ykcryobs.ptportal.domain.preferences.DefaultConnectionMode
import cn.ykcryobs.ptportal.domain.preferences.ThemeMode
import cn.ykcryobs.ptportal.domain.preferences.TransferQuality
import cn.ykcryobs.ptportal.domain.preferences.UserPreferencesRepository

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
) : ViewModel() {
    val preferences = repository.preferences

    fun setDefaultConnectionMode(mode: DefaultConnectionMode) =
        repository.setDefaultConnectionMode(mode)

    fun setAutoReconnect(enabled: Boolean) = repository.setAutoReconnect(enabled)

    fun setScanIntervalSeconds(seconds: Int) = repository.setScanIntervalSeconds(seconds)

    fun setTransferQuality(quality: TransferQuality) = repository.setTransferQuality(quality)

    fun setThemeMode(mode: ThemeMode) = repository.setThemeMode(mode)

    fun setLanguage(language: AppLanguage) = repository.setLanguage(language)
}
