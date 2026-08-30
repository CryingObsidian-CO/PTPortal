package cn.ykcryobs.ptportal.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cn.ykcryobs.ptportal.domain.preferences.UserPreferencesRepository
import cn.ykcryobs.ptportal.domain.connection.CameraConnectionRepository

private val BottomNavMaxWidth = 600
private val RailNavMaxWidth = 840

@Composable
fun PTScaffold(
    userPreferencesRepository: UserPreferencesRepository,
    connectionRepository: CameraConnectionRepository,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val configuration = LocalConfiguration.current
    val screenWidthDp = LocalWindowInfo.current.containerSize.width
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val useBottomBar = screenWidthDp < BottomNavMaxWidth || !isLandscape
    val useRail = !useBottomBar && screenWidthDp < RailNavMaxWidth
    val useExpandedRail = !useBottomBar && screenWidthDp >= RailNavMaxWidth

    if (useBottomBar) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    PTDestinations.entries.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = stringResource(destination.labelRes),
                                )
                            },
                            label = { Text(stringResource(destination.labelRes)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            },
        ) { innerPadding ->
            PTNavHost(
                navController = navController,
                userPreferencesRepository = userPreferencesRepository,
                connectionRepository = connectionRepository,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                PTDestinations.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == destination.route
                    } == true
                    NavigationRailItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = stringResource(destination.labelRes),
                            )
                        },
                        label = if (useExpandedRail) {
                            { Text(stringResource(destination.labelRes)) }
                        } else null,
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
            PTNavHost(
                navController = navController,
                userPreferencesRepository = userPreferencesRepository,
                connectionRepository = connectionRepository,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            )
        }
    }
}
