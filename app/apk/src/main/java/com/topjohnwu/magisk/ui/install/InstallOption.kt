package com.topjohnwu.magisk.ui.install

import com.topjohnwu.magisk.core.Info
import kotlinx.serialization.Serializable

@Serializable
sealed class InstallOptions {

    abstract val isAvailable: Boolean

    @Serializable
    object KeepVerity : InstallOptions() {
        override val isAvailable get() = !Info.isSAR
    }

    @Serializable
    object KeepEncryption : InstallOptions() {
        override val isAvailable get() = !Info.isFDE
    }

    @Serializable
    object Recovery : InstallOptions() {
        override val isAvailable get() = !Info.ramdisk
    }

}
