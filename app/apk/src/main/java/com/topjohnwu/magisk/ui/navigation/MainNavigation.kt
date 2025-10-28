package com.topjohnwu.magisk.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.topjohnwu.magisk.ui.action.ActionParams
import com.topjohnwu.magisk.ui.action.ActionScreen
import com.topjohnwu.magisk.ui.action.ActionViewModel
import com.topjohnwu.magisk.ui.flash.FlashParams
import com.topjohnwu.magisk.ui.flash.FlashScreen
import com.topjohnwu.magisk.ui.flash.FlashViewModel
import com.topjohnwu.magisk.ui.home.HomeScreen
import com.topjohnwu.magisk.ui.home.HomeViewModel
import com.topjohnwu.magisk.ui.install.InstallScreen
import com.topjohnwu.magisk.ui.install.InstallViewModel
import com.topjohnwu.magisk.ui.module.ModuleScreen
import com.topjohnwu.magisk.ui.module.ModuleViewModel
import com.topjohnwu.magisk.ui.navigation.MainDestination.*
import com.topjohnwu.magisk.ui.settings.SettingsScreen
import com.topjohnwu.magisk.ui.settings.SettingsViewModel
import com.topjohnwu.magisk.ui.superuser.SuperUserScreen
import com.topjohnwu.magisk.ui.superuser.SuperUserViewModel
import me.edgeatzero.compose.util.ProvideUIMode
import me.edgeatzero.compose.util.UIMode
import me.edgeatzero.compose.util.dynamicBarColor
import me.edgeatzero.compose.util.none
import org.kodein.di.compose.rememberViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    Surface {
        when (UIMode.current) {
            UIMode.MOBILE -> MobileView(navController)
            UIMode.TABLET -> TabletView(navController)
        }
    }
}

@Composable
private fun <T> BadgeNavigationIcon(
    destination: T,
    isSelected: Boolean
) where T : Destination, T : Extra {
    BadgedBox(
        badge = {
            val badge = when (destination) {
                SuperUser -> SuperUserViewModel.badge
                Module -> ModuleViewModel.badge
                else -> null
            }
            if (badge != null) {
                if (badge != -1) {
                    Badge { Text(text = badge.toString()) }
                } else {
                    Badge()
                }
            }
        }
    ) {
        if (isSelected) {
            Icon(imageVector = destination.selected, contentDescription = null)
        } else {
            Icon(imageVector = destination.unselected, contentDescription = null)
        }
    }
}

@Composable
private fun MobileView(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = MainDestinations.any { route == it.route },
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .dynamicBarColor()
                            .navigationBarsPadding(),
                        containerColor = Color.Transparent
                    ) {
                        MainDestinations.forEach { destination ->
                            if (destination !is Extra) return@forEach
                            val isSelected = route == destination.route

                            NavigationBarItem(
                                icon = { BadgeNavigationIcon(destination, isSelected) },
                                label = { Text(destination.label) },
                                alwaysShowLabel = false,
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(destination) {
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
            }
        },
        contentWindowInsets = WindowInsets.none
    ) { contentPadding ->
        MainNavigationHost(navController, contentPadding)
    }
}

@Composable
private fun TabletView(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    Scaffold(contentWindowInsets = WindowInsets.none) { contentPadding ->
        Row {
            AnimatedVisibility(
                visible = MainDestinations.any { route == it.route },
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
            ) {
                NavigationRail(
                    modifier = Modifier
                        .dynamicBarColor()
                        .navigationBarsPadding(),
                    containerColor = Color.Transparent,
                ) {
                    MainDestinations.forEach { destination ->
                        if (destination !is Extra) return@forEach
                        val isSelected = route == destination.route

                        NavigationRailItem(
                            icon = { BadgeNavigationIcon(destination, isSelected) },
                            label = { Text(destination.label) },
                            alwaysShowLabel = false,
                            selected = isSelected,
                            onClick = {
                                navController.navigate(destination) {
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
            MainNavigationHost(navController, contentPadding)
        }
    }
}

@Composable
private fun MainNavigationHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints {
        ProvideUIMode(if (maxWidth >= 840.dp) UIMode.TABLET else UIMode.MOBILE) {
            NavHost(
                modifier = modifier,
                navController = navController,
                startDestination = Home,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                composable<Home>(deepLinks = listOf(Home.deeplink), typeMap = Home.typeMap) {
                    val viewModel by rememberViewModel<HomeViewModel>()
                    HomeScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel,
                        onNavigateToInstall = { navController.navigate(Install) },
                        onNavigateToUninstall = { navController.navigate(Flash()) }
                    )
                }
                composable<SuperUser>(deepLinks = listOf(SuperUser.deeplink), typeMap = SuperUser.typeMap) {
                    val viewModel by rememberViewModel<SuperUserViewModel>()
                    SuperUserScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel
                    )
                }
                composable<Module>(deepLinks = listOf(Module.deeplink), typeMap = Module.typeMap) {
                    val viewModel by rememberViewModel<ModuleViewModel>()
                    ModuleScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel,
                        onModuleAction = { p0, p1 ->
                            navController.navigate(Action(p0, p1))
                        },
                        onModuleInstall = {
                            navController.navigate(Flash(it))
                        }
                    )
                }
                composable<Settings>(deepLinks = listOf(Settings.deeplink), typeMap = Settings.typeMap) {
                    val viewModel by rememberViewModel<SettingsViewModel>()
                    SettingsScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel
                    )
                }
                composable<Action> { backStackEntry ->
                    val argument = backStackEntry.toRoute<Action>().let(::ActionParams)
                    val viewModel by rememberViewModel<ActionParams, ActionViewModel>(arg = argument)
                    ActionScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel
                    )
                }
                composable<Flash>(deepLinks = listOf(Flash.deeplink), typeMap = Flash.typeMap) { backStackEntry ->
                    val argument = backStackEntry.toRoute<Flash>().action
                    val viewModel by rememberViewModel<FlashParams, FlashViewModel>(arg = argument)
                    FlashScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel
                    )
                }
                composable<Install> {
                    val viewModel by rememberViewModel<InstallViewModel>()
                    InstallScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel,
                        onNavigateToFlash = { p0, p1, p2 ->
                            navController.navigateUp()
                            navController.navigate(Flash(p0, ArrayList(p1), p2))
                        }
                    )
                }
            }
        }
    }
}
