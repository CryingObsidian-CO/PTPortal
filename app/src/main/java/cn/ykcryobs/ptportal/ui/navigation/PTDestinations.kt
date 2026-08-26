package cn.ykcryobs.ptportal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SettingsRemote
import androidx.compose.ui.graphics.vector.ImageVector
import cn.ykcryobs.ptportal.R

enum class PTDestinations(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Connection(
        route = "connection",
        labelRes = R.string.nav_connection,
        selectedIcon = Icons.Filled.PhotoCamera,
        unselectedIcon = Icons.Outlined.PhotoCamera,
    ),
    Remote(
        route = "remote",
        labelRes = R.string.nav_remote,
        selectedIcon = Icons.Filled.SettingsRemote,
        unselectedIcon = Icons.Outlined.SettingsRemote,
    ),
    Files(
        route = "files",
        labelRes = R.string.nav_files,
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder,
    ),
    Settings(
        route = "settings",
        labelRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    );

    companion object {
        fun fromRoute(route: String?): PTDestinations =
            entries.find { it.route == route } ?: Connection
    }
}
