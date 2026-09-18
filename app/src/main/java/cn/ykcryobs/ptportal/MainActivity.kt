package cn.ykcryobs.ptportal

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.ykcryobs.ptportal.di.AppContainer
import cn.ykcryobs.ptportal.domain.preferences.ThemeMode
import cn.ykcryobs.ptportal.ptp.common.PtpConstants
import cn.ykcryobs.ptportal.ui.navigation.PTScaffold
import cn.ykcryobs.ptportal.ui.theme.PTPortalTheme

class MainActivity : ComponentActivity() {

    private lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(PtpConstants.LOG_TAG, "App onCreate 启动成功！！！")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppContextHolder.init(this)
        container = AppContainer(this)
        container.usbCameraRepository.start()

        setContent {
            val preferences by container.preferencesRepository.preferences.collectAsState()
            val systemUsesDarkTheme = isSystemInDarkTheme()
            val useDarkTheme = when (preferences.themeMode) {
                ThemeMode.System -> systemUsesDarkTheme
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }

            PTPortalTheme(darkTheme = useDarkTheme) {
                PTScaffold(
                    userPreferencesRepository = container.preferencesRepository,
                    connectionRepository = container.connectionRepository,
                    cameraControlRepository = container.cameraControlRepository,
                )
            }
        }
    }

    override fun onDestroy() {
        container.release()
        super.onDestroy()
    }
}
