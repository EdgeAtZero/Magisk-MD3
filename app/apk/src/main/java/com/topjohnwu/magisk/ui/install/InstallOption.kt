package com.topjohnwu.magisk.ui.install

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.install.InstallOption.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.collections.ArrayList

@OptIn(ExperimentalSerializationApi::class)
@Serializable
sealed class InstallOption() {

    abstract val isAvailable: Boolean

    protected abstract val serialName: String

    @Serializable
    object KeepVerity : InstallOption() {
        override val isAvailable get() = !Info.isSAR
        override val serialName by lazy { serializer().descriptor.serialName }
    }

    @Serializable
    object KeepEncryption : InstallOption() {
        override val isAvailable get() = !Info.isFDE
        override val serialName by lazy { serializer().descriptor.serialName }
    }

    @Serializable
    object Recovery : InstallOption() {
        override val isAvailable get() = !Info.ramdisk
        override val serialName by lazy { serializer().descriptor.serialName }
    }

    companion object : NavType<ArrayList<InstallOption>>(isNullableAllowed = true) {

        override fun get(bundle: SavedState, key: String): ArrayList<InstallOption> =
            bundle.getString(key)?.let { parseValue(it) } ?: ArrayList()

        override fun put(bundle: SavedState, key: String, value: ArrayList<InstallOption>) =
            bundle.putString(key, serializeAsValue(value))

        override fun parseValue(value: String): ArrayList<InstallOption> =
            Json.decodeFromString(value)

        override fun serializeAsValue(value: ArrayList<InstallOption>): String =
            Json.encodeToString(value)

    }

}

val InstallOptions = listOf(KeepVerity, KeepEncryption, Recovery)
