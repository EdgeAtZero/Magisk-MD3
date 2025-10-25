package com.topjohnwu.magisk.ui.navigation

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
import com.topjohnwu.magisk.ui.install.InstallMethod
import com.topjohnwu.magisk.ui.install.InstallOption
import com.topjohnwu.magisk.ui.navigation.MainDestination.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
sealed class MainDestination {

    abstract val route: String
    abstract val label: String
    abstract val selected: ImageVector
    abstract val unselected: ImageVector

    open val isRootNeed = false

    @Serializable
    sealed class ParentDestination() : MainDestination() {
        override val label get() = throw NotImplementedError()
        override val selected get() = throw NotImplementedError()
        override val unselected get() = throw NotImplementedError()
        override val route get() = ""
    }

    @Serializable
    sealed class ChildDestination() : MainDestination() {
        override val label get() = throw NotImplementedError()
        override val selected get() = throw NotImplementedError()
        override val unselected get() = throw NotImplementedError()
    }

    @Serializable
    data object HomeNav : ParentDestination() {

        @Serializable
        data object Main : MainDestination() {
            override val label = "主页"
            override val route by lazy { serializer().descriptor.serialName }
            override val selected = Icons.Filled.Home
            override val unselected = Icons.Outlined.Home
        }

        @Serializable
        data object Install : ChildDestination() {
            override val route by lazy { serializer().descriptor.serialName }
        }

        @Serializable
        data object Uninstall : ChildDestination() {
            override val route by lazy { serializer().descriptor.serialName }
        }

        @Serializable
        data class Flash(
            val method: InstallMethod,
            val options: ArrayList<InstallOption> = ArrayList(),
            val patchFile: String? = null,
        ) : ChildDestination() {
            override val route by lazy { serializer().descriptor.serialName }
        }

    }

    @Serializable
    data object SuperUser : MainDestination() {
        override val label = "超级用户"
        override val route by lazy { serializer().descriptor.serialName }
        override val selected = Icons.Filled.AdminPanelSettings
        override val unselected = Icons.Outlined.AdminPanelSettings
        override val isRootNeed = true
    }

    @Serializable
    data object ModuleNav : ParentDestination() {

        @Serializable
        data object Main : MainDestination() {
            override val label = "模块"
            override val route by lazy { serializer().descriptor.serialName }
            override val selected = Icons.Filled.Layers
            override val unselected = Icons.Outlined.Layers
            override val isRootNeed = true
        }

        @Serializable
        data class Action(
            val id: String,
            val name: String
        ) : ChildDestination() {
            override val route by lazy { serializer().descriptor.serialName }
        }

        @Serializable
        data class Install(
            val uri: String
        ) : ChildDestination() {
            override val route by lazy { serializer().descriptor.serialName }
        }

    }

    @Serializable
    data object Settings : MainDestination() {
        override val label = "设置"
        override val route by lazy { serializer().descriptor.serialName }
        override val selected = Icons.Filled.Settings
        override val unselected = Icons.Outlined.Settings
    }

}

val MainDestinations = listOf(HomeNav.Main, SuperUser, ModuleNav.Main, Settings)

fun List<MainDestination>.contains(route: String?): Boolean =
    this.any { it.route == route }
