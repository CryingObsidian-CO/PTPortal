package cn.ykcryobs.ptportal.data.preferences

import android.content.Context
import android.content.SharedPreferences
import cn.ykcryobs.ptportal.domain.preferences.AppLanguage
import cn.ykcryobs.ptportal.domain.preferences.DefaultConnectionMode
import cn.ykcryobs.ptportal.domain.preferences.ThemeMode
import cn.ykcryobs.ptportal.domain.preferences.TransferQuality
import cn.ykcryobs.ptportal.domain.preferences.UserPreferences
import cn.ykcryobs.ptportal.domain.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedPreferencesUserPreferencesRepository(
    context: Context,
) : UserPreferencesRepository {

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    override val preferences: StateFlow<UserPreferences> = _preferences

    override fun setDefaultConnectionMode(mode: DefaultConnectionMode) {
        persistEnum(KEY_CONNECTION_MODE, mode.name)
        _preferences.value = _preferences.value.copy(defaultConnectionMode = mode)
    }

    override fun setAutoReconnect(enabled: Boolean) {
        persistBoolean(KEY_AUTO_RECONNECT, enabled)
        _preferences.value = _preferences.value.copy(autoReconnect = enabled)
    }

    override fun setScanIntervalSeconds(seconds: Int) {
        val validSeconds = seconds.coerceIn(MIN_SCAN_INTERVAL_SECONDS, MAX_SCAN_INTERVAL_SECONDS)
        persistInt(KEY_SCAN_INTERVAL_SECONDS, validSeconds)
        _preferences.value = _preferences.value.copy(scanIntervalSeconds = validSeconds)
    }

    override fun setTransferQuality(quality: TransferQuality) {
        persistEnum(KEY_TRANSFER_QUALITY, quality.name)
        _preferences.value = _preferences.value.copy(transferQuality = quality)
    }

    override fun setThemeMode(mode: ThemeMode) {
        persistEnum(KEY_THEME_MODE, mode.name)
        _preferences.value = _preferences.value.copy(themeMode = mode)
    }

    override fun setLanguage(language: AppLanguage) {
        persistEnum(KEY_LANGUAGE, language.name)
        _preferences.value = _preferences.value.copy(language = language)
    }

    private fun loadPreferences(): UserPreferences {
        return UserPreferences(
            defaultConnectionMode = sharedPreferences.getEnum(
                KEY_CONNECTION_MODE,
                DefaultConnectionMode.Auto,
            ),
            autoReconnect = sharedPreferences.getBoolean(KEY_AUTO_RECONNECT, true),
            scanIntervalSeconds = sharedPreferences.getInt(
                KEY_SCAN_INTERVAL_SECONDS,
                DEFAULT_SCAN_INTERVAL_SECONDS,
            ),
            transferQuality = sharedPreferences.getEnum(
                KEY_TRANSFER_QUALITY,
                TransferQuality.High,
            ),
            themeMode = sharedPreferences.getEnum(KEY_THEME_MODE, ThemeMode.System),
            language = sharedPreferences.getEnum(KEY_LANGUAGE, AppLanguage.System),
        )
    }

    private fun persistEnum(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun persistBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun persistInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    private inline fun <reified T : Enum<T>> SharedPreferences.getEnum(
        key: String,
        defaultValue: T,
    ): T {
        val storedName = getString(key, null) ?: return defaultValue
        return enumValues<T>().firstOrNull { it.name == storedName } ?: defaultValue
    }

    private companion object {
        const val PREFERENCES_NAME = "pt_portal_user_preferences"
        const val KEY_CONNECTION_MODE = "default_connection_mode"
        const val KEY_AUTO_RECONNECT = "auto_reconnect"
        const val KEY_SCAN_INTERVAL_SECONDS = "scan_interval_seconds"
        const val KEY_TRANSFER_QUALITY = "transfer_quality"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_LANGUAGE = "app_language"
        const val MIN_SCAN_INTERVAL_SECONDS = 2
        const val DEFAULT_SCAN_INTERVAL_SECONDS = 5
        const val MAX_SCAN_INTERVAL_SECONDS = 10
    }
}
