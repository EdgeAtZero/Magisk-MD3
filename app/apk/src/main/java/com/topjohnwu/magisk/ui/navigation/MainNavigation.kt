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
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.action.ActionParams
import com.topjohnwu.magisk.ui.action.ActionScreen
import com.topjohnwu.magisk.ui.action.ActionViewModel
import com.topjohnwu.magisk.ui.flash.FlashAction
import com.topjohnwu.magisk.ui.flash.FlashScreen
import com.topjohnwu.magisk.ui.flash.FlashViewModel
import com.topjohnwu.magisk.ui.home.HomeScreen
import com.topjohnwu.magisk.ui.home.HomeViewModel
import com.topjohnwu.magisk.ui.install.InstallMethod
import com.topjohnwu.magisk.ui.install.InstallOption
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
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.reflect.typeOf

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
private fun BadgeNavigationIcon(
    destination: MainDestination,
    isSelected: Boolean,
    isBeSelected: Boolean
): Unit = with(localDI().direct) {
    BadgedBox(
        badge = {
            if (remember { destination in listOf(ModuleNav.Main, SuperUser) }) {
                when {
                    isBeSelected && destination == ModuleNav.Main -> remember(this) { instance<ModuleViewModel>() }.badge

                    isBeSelected && destination == SuperUser -> remember(this) { instance<SuperUserViewModel>() }.badge

                    else -> null
                }?.let { Badge { Text(text = it) } } ?: Badge()
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
                    visible = MainDestinations.contains(route),
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .dynamicBarColor()
                            .navigationBarsPadding(),
                        containerColor = Color.Transparent
                    ) {
                        MainDestinations.filter { !it.isRootNeed || Info.env.isActive }.forEach { destination ->
                            val isSelected = route == destination.route
                            var isBeSelected by rememberSaveable { mutableStateOf(false) }

                            LaunchedEffect(isSelected) { if (isSelected) isBeSelected = true }

                            NavigationBarItem(
                                icon = { BadgeNavigationIcon(destination, isSelected, isBeSelected) },
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
                visible = MainDestinations.contains(route),
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
            ) {
                NavigationRail(
                    modifier = Modifier
                        .dynamicBarColor()
                        .navigationBarsPadding(),
                    containerColor = Color.Transparent,
                ) {
                    MainDestinations.filter { !it.isRootNeed || Info.env.isActive }.forEach { destination ->
                        val isSelected = route == destination.route
                        var isBeSelected by rememberSaveable { mutableStateOf(false) }

                        LaunchedEffect(isSelected) { if (isSelected) isBeSelected = true }

                        NavigationRailItem(
                            icon = { BadgeNavigationIcon(destination, isSelected, isBeSelected) },
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
                startDestination = HomeNav,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                navigation<HomeNav>(startDestination = HomeNav.Main) {
                    composable<HomeNav.Main> {
                        val viewModel by rememberViewModel<HomeViewModel>()
                        HomeScreen(
                            rootContentPadding = contentPadding,
                            viewModel = viewModel,
                            onNavigateToInstall = {
                                navController.navigate(HomeNav.Install)
                            }
                        )
                    }
                    composable<HomeNav.Uninstall> {
                        val viewModel by rememberViewModel<FlashViewModel>(FlashAction.Uninstall)
                        FlashScreen(
                            rootContentPadding = contentPadding,
                            viewModel = viewModel
                        )
                    }
                    composable<HomeNav.Install> {
                        val viewModel by rememberViewModel<InstallViewModel>()
                        InstallScreen(
                            rootContentPadding = contentPadding,
                            viewModel = viewModel,
                            onNavigateToFlash = { p0, p1, p2 ->
                                navController.navigateUp()
                                navController.navigate(HomeNav.Flash(p0, ArrayList(p1), p2))
                            }
                        )
                    }
                    composable<HomeNav.Flash>(
                        typeMap = mapOf(
                            typeOf<InstallMethod>() to InstallMethod.Companion,
                            typeOf<ArrayList<InstallOption>>() to InstallOption.Companion
                        )
                    ) { backStackEntry ->
                        val argument = backStackEntry.toRoute<HomeNav.Flash>().let(FlashAction::Install)
                        val viewModel by rememberViewModel<FlashAction.Install, FlashViewModel>(arg = argument)
                        FlashScreen(
                            rootContentPadding = contentPadding,
                            viewModel = viewModel
                        )
                    }
                }
                composable<SuperUser> {
                    val viewModel by rememberViewModel<SuperUserViewModel>()
                    SuperUserScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel
                    )
                }
                navigation<ModuleNav>(startDestination = ModuleNav.Main) {
                    composable<ModuleNav.Main> {
                        val viewModel by rememberViewModel<ModuleViewModel>()
                        ModuleScreen(
                            rootContentPadding = contentPadding,
                            viewModel = viewModel,
                            onModuleAction = { p0, p1 ->
                                navController.navigate(ModuleNav.Action(p0, p1))
                            },
                            onModuleInstall = {
                                navController.navigate(ModuleNav.Install(it))
                            }
                        )
                    }
                    composable<ModuleNav.Action> { backStackEntry ->
                        val argument = backStackEntry.toRoute<ModuleNav.Action>().let(::ActionParams)
                        val viewModel by rememberViewModel<ActionParams, ActionViewModel>(arg = argument)
                        ActionScreen(
                            rootContentPadding = contentPadding,
                            viewModel = viewModel
                        )
                    }
                    composable<ModuleNav.Install> { backStackEntry ->
                        val argument = backStackEntry.toRoute<ModuleNav.Install>().let(FlashAction::Module)
                        val viewModel by rememberViewModel<FlashAction, FlashViewModel>(arg = argument)
                        FlashScreen(
                            rootContentPadding = contentPadding,
                            viewModel = viewModel
                        )
                    }
                }
                composable<Settings> {
                    val viewModel by rememberViewModel<SettingsViewModel>()
                    SettingsScreen(
                        rootContentPadding = contentPadding,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
