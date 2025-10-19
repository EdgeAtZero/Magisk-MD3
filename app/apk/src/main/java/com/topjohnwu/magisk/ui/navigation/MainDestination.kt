package com.topjohnwu.magisk.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.topjohnwu.magisk.ui.nav.MainDestination.Home
import com.topjohnwu.magisk.ui.nav.MainDestination.Module
import com.topjohnwu.magisk.ui.nav.MainDestination.Settings
import com.topjohnwu.magisk.ui.nav.MainDestination.Superuser

sealed class MainDestination(
    val route: String,
    val label: String,
    val selected: ImageVector,
    val unselected: ImageVector
) {

    object Home : MainDestination("home", "主页", Icons.Filled.Home, Icons.Outlined.Home)

    object Superuser : MainDestination("superuser", "超级用户", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)

    object Module : MainDestination("module", "模块", Icons.Filled.Layers, Icons.Outlined.Layers)

    object Settings : MainDestination("settings", "设置", Icons.Filled.Settings, Icons.Outlined.Settings)

}

val MainDestinations = listOf(Home, Superuser, Module, Settings)
