@file:OptIn(InternalApi::class, InternalSerializationApi::class)

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
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navDeepLink
import com.topjohnwu.magisk.InternalApi
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.flash.FlashParams
import com.topjohnwu.magisk.ui.install.InstallMethod
import com.topjohnwu.magisk.ui.install.InstallOption
import com.topjohnwu.magisk.ui.navigation.MainDestination.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import me.edgeatzero.arch.util.navType
import me.edgeatzero.arch.util.toSnakeCase
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

@Serializable
sealed class MainDestination {

    sealed class Destination {

        val route by lazy { this::class.serializer().descriptor.serialName }

        sealed interface Companion<T : Destination> {

            val deeplink: NavDeepLink?

            val typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>

        }

    }

    sealed interface Extra {

        val label: String
        val selected: ImageVector
        val unselected: ImageVector

    }

    @Serializable
    data object Home : Destination(), Destination.Companion<Home>, Extra {

        override val label get() = "主页"
        override val selected get() = Icons.Filled.Home
        override val unselected get() = Icons.Outlined.Home

        override val deeplink by deeplink()
        override val typeMap get() = emptyMap<KType, NavType<*>>()

    }

    @Serializable
    data object SuperUser : Destination(), Destination.Companion<SuperUser>, Extra {

        override val label get() = "超级用户"
        override val selected get() = Icons.Filled.AdminPanelSettings
        override val unselected get() = Icons.Outlined.AdminPanelSettings

        override val deeplink by deeplink()
        override val typeMap get() = emptyMap<KType, NavType<*>>()

    }

    @Serializable
    data object Module : Destination(), Destination.Companion<Module>, Extra {

        override val label get() = "模块"
        override val selected get() = Icons.Filled.Layers
        override val unselected get() = Icons.Outlined.Layers

        override val deeplink by deeplink()
        override val typeMap get() = emptyMap<KType, NavType<*>>()

    }

    @Serializable
    data object Settings : Destination(), Destination.Companion<Settings>, Extra {

        override val label get() = "设置"
        override val selected get() = Icons.Filled.Settings
        override val unselected get() = Icons.Outlined.Settings

        override val deeplink by deeplink()
        override val typeMap get() = emptyMap<KType, NavType<*>>()

    }

    @Serializable
    data class Action(
        val id: String,
        val name: String
    ) : Destination()

    @Serializable
    data class Flash(val action: FlashParams) : Destination() {

        constructor(
            file: String
        ) : this(FlashParams.Module(file))

        constructor(
            method: InstallMethod,
            options: ArrayList<InstallOption>,
            patchFile: String? = null
        ) : this(FlashParams.Install(method, options, patchFile))

        constructor(
        ) : this(FlashParams.Uninstall)

        companion object : Destination.Companion<Flash> {
            override val deeplink by deeplink()
            override val typeMap get() = mapOf(navType<FlashParams>())
        }


    }

    @Serializable
    data object Install : Destination()

    companion object {

        const val DEEPLINK = "magisk://main"

    }

}

val MainDestinations: List<Destination>
    get() = if (Info.isRooted) listOf(Home, SuperUser, Module, Settings) else listOf(Home, Settings)

inline fun <reified T : Destination> Destination.Companion<T>.deeplink(): Lazy<NavDeepLink> = lazy {
    navDeepLink<T>(basePath = MainDestination.DEEPLINK + T::class.toDeeplinkPath(), typeMap)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> NavDeepLink.build(
    data: T,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>
): String = buildString {
    val pattern = requireNotNull(uriPattern) { "missing uri pattern" }
    val klass = T::class
    val primaryConstructor = requireNotNull(klass.primaryConstructor) { "no primary constructor" }
    val properties = primaryConstructor.parameters.associate { parameter ->
        parameter.name to klass.declaredMemberProperties.first {
            it.name == parameter.name
        }
    }
    val navTypes = typeMap as Map<KType, NavType<Any?>>
    var si = -1
    pattern.forEachIndexed { i, c ->
        if (si == -1) {
            if (c == '{') {
                si = i
            } else {
                append(c)
            }
        } else {
            if (c == '}') {
                val key = pattern.substring(si + 1, i)
                val property = requireNotNull(properties[key])
                val obj = property.getter.call(data)
                val value = navTypes[property.returnType]?.serializeAsValue(obj)
                    ?: Json.encodeToString(Json.serializersModule.serializer(property.getter.returnType), obj)
                append(value)
                si = -1
            } else {
                // 丢弃
            }
        }
    }
}

fun <T : Any> KClass<T>.toDeeplinkPath(): String =
    serializer().descriptor.serialName
        .substringAfter(MainDestination.serializer().descriptor.serialName)
        .split('.')
        .filterNot { it.equals("main", ignoreCase = true) }
        .map { it.substringBeforeLast("Nav") }
        .joinToString(separator = "/") { it.toSnakeCase() }
