package com.topjohnwu.magisk.ui.install

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.install.InstallMethod.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed class InstallMethod {

    abstract val isAvailable: Boolean

    @Serializable
    object Patch : InstallMethod() {
        override val isAvailable get() = true
    }

    @Serializable
    object Direct : InstallMethod() {
        override val isAvailable get() = Info.isRooted
    }

    @Serializable
    object InactiveSlot : InstallMethod() {
        override val isAvailable get() = Info.isRooted && Info.isAB && !Info.isEmulator
    }

    companion object : NavType<InstallMethod>(isNullableAllowed = false) {

        override fun get(bundle: SavedState, key: String): InstallMethod? =
            bundle.getString(key)?.let { parseValue(it) }

        override fun put(bundle: SavedState, key: String, value: InstallMethod) =
            bundle.putString(key, serializeAsValue(value))

        override fun parseValue(value: String): InstallMethod =
            Json.decodeFromString(value)

        override fun serializeAsValue(value: InstallMethod): String =
            Json.encodeToString(value)

    }

}

val InstallMethods = listOf(Patch, Direct, InactiveSlot)
