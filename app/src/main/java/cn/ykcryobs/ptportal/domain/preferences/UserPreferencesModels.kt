package cn.ykcryobs.ptportal.domain.preferences

import kotlinx.coroutines.flow.StateFlow

enum class DefaultConnectionMode {
    Auto,
    Usb,
    Wifi,
}

enum class TransferQuality {
    Standard,
    High,
    Original,
}

enum class ThemeMode {
    System,
    Light,
    Dark,
}

enum class AppLanguage {
    System,
    Chinese,
    English,
}

// TODO: 连接流程接入真实仓库时读取默认连接方式和自动重连设置。
data class UserPreferences(
    val defaultConnectionMode: DefaultConnectionMode = DefaultConnectionMode.Auto,
    val autoReconnect: Boolean = true,
    val scanIntervalSeconds: Int = 5,
    val transferQuality: TransferQuality = TransferQuality.High,
    val themeMode: ThemeMode = ThemeMode.System,
    val language: AppLanguage = AppLanguage.System,
) {
    companion object {
        const val DEFAULT_SAVE_PATH_HINT = "/Android/media/cn.ykcryobs.ptportal/Captures"
    }
}

interface UserPreferencesRepository {
    val preferences: StateFlow<UserPreferences>

    fun setDefaultConnectionMode(mode: DefaultConnectionMode)
    fun setAutoReconnect(enabled: Boolean)
    fun setScanIntervalSeconds(seconds: Int)
    fun setTransferQuality(quality: TransferQuality)
    fun setThemeMode(mode: ThemeMode)
    fun setLanguage(language: AppLanguage)
}
