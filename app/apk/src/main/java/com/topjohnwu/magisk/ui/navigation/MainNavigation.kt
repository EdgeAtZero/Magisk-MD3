package com.topjohnwu.magisk.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.topjohnwu.magisk.ui.home.HomeScreen
import com.topjohnwu.magisk.ui.module.ModuleScreen
import com.topjohnwu.magisk.ui.settings.SettingsScreen
import com.topjohnwu.magisk.ui.superuser.SuperUserScreen
import com.topjohnwu.magisk.ui.util.LocalNavHostController
import com.topjohnwu.magisk.ui.util.LocalSnackbarHost
import com.topjohnwu.magisk.ui.util.dynamicBarBackgroundColor
import me.edgeatzero.compose.util.UIMode

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    Surface {
        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalSnackbarHost provides snackbarHostState
        ) {
            when (UIMode.current) {
                UIMode.MOBILE -> PortraitView()
                UIMode.TABLET -> LandscapeView()
            }
        }
    }
}

@Composable
private fun PortraitView() {
    val navController = LocalNavHostController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        snackbarHost = {
            SnackbarHost(LocalSnackbarHost.current)
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .dynamicBarBackgroundColor()
                    .navigationBarsPadding(),
                containerColor = Color.Transparent,
            ) {
                MainDestinations.forEach { destination ->
                    val isSelected = currentDestination?.route == destination.route

                    NavigationBarItem(
                        icon = {
                            if (isSelected) {
                                Icon(destination.selected, destination.label)
                            } else {
                                Icon(destination.unselected, destination.label)
                            }
                        },
                        label = { Text(destination.label) },
                        alwaysShowLabel = false,
                        selected = isSelected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        MainNavigationHost()
    }
}

@Composable
private fun LandscapeView() {
    val navController = LocalNavHostController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Row {
        NavigationRail(
            modifier = Modifier
                .dynamicBarBackgroundColor()
                .safeContentPadding(),
            containerColor = Color.Transparent,
        ) {
            MainDestinations.forEach { destination ->
                val isSelected = currentDestination?.route == destination.route

                NavigationRailItem(
                    icon = {
                        if (isSelected) {
                            Icon(destination.selected, destination.label)
                        } else {
                            Icon(destination.unselected, destination.label)
                        }
                    },
                    label = { Text(destination.label) },
                    alwaysShowLabel = false,
                    selected = isSelected,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
        MainNavigationHost()
    }
}

@Composable
private fun MainNavigationHost() {
    val innerPadding = PaddingValues(0.dp)
    NavHost(
        navController = LocalNavHostController.current,
        startDestination = MainDestination.Home.route
    ) {
        composable(MainDestination.Home.route) {
            HomeScreen()
        }
        composable(MainDestination.Superuser.route) {
            SuperUserScreen()
        }
        composable(MainDestination.Module.route) {
            ModuleScreen()
        }
        composable(MainDestination.Settings.route) {
            SettingsScreen()
        }
    }
}
