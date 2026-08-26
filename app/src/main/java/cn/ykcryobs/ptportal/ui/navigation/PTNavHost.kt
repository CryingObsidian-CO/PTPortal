package cn.ykcryobs.ptportal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cn.ykcryobs.ptportal.domain.preferences.UserPreferencesRepository
import cn.ykcryobs.ptportal.ui.screens.connection.ConnectionScreen
import cn.ykcryobs.ptportal.ui.screens.files.FilesScreen
import cn.ykcryobs.ptportal.ui.screens.remote.RemoteScreen
import cn.ykcryobs.ptportal.ui.screens.settings.SettingsScreen

@Composable
fun PTNavHost(
    navController: NavHostController,
    userPreferencesRepository: UserPreferencesRepository,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = PTDestinations.Connection.route,
        modifier = modifier,
    ) {
        composable(PTDestinations.Connection.route) {
            ConnectionScreen()
        }
        composable(PTDestinations.Remote.route) {
            RemoteScreen()
        }
        composable(PTDestinations.Files.route) {
            FilesScreen()
        }
        composable(PTDestinations.Settings.route) {
            SettingsScreen(repository = userPreferencesRepository)
        }
    }
}
