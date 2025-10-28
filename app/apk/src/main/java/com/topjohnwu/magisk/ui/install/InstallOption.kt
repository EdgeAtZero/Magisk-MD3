package com.topjohnwu.magisk.ui.install

import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.install.InstallOption.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

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

}

val InstallOptions = listOf(KeepVerity, KeepEncryption, Recovery)
