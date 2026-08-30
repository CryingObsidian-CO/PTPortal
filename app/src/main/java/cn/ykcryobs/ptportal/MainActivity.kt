package cn.ykcryobs.ptportal

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.ykcryobs.ptportal.data.connection.UsbCameraConnectionRepository
import cn.ykcryobs.ptportal.data.preferences.SharedPreferencesUserPreferencesRepository
import cn.ykcryobs.ptportal.domain.preferences.ThemeMode
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ui.navigation.PTScaffold
import cn.ykcryobs.ptportal.ui.theme.PTPortalTheme

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: SharedPreferencesUserPreferencesRepository
    private lateinit var connectionRepository: UsbCameraConnectionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(PtpConstants.LOG_TAG, "App onCreate 启动成功！！！")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppContextHolder.init(this)
        userPreferencesRepository = SharedPreferencesUserPreferencesRepository(this)
        connectionRepository = UsbCameraConnectionRepository(this)
        connectionRepository.start()

        setContent {
            val preferences by userPreferencesRepository.preferences.collectAsState()
            val systemUsesDarkTheme = isSystemInDarkTheme()
            val useDarkTheme = when (preferences.themeMode) {
                ThemeMode.System -> systemUsesDarkTheme
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }

            PTPortalTheme(darkTheme = useDarkTheme) {
                PTScaffold(userPreferencesRepository, connectionRepository)
            }
        }
    }

    override fun onDestroy() {
        connectionRepository.release()
        super.onDestroy()
    }
}
