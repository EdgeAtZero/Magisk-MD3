package com.topjohnwu.magisk.ui.flash

import com.topjohnwu.magisk.ui.install.InstallMethod
import com.topjohnwu.magisk.ui.install.InstallOption
import kotlinx.serialization.Serializable

@Serializable
sealed class FlashParams {

    @Serializable
    data class Install(
        val method: InstallMethod,
        val options: ArrayList<InstallOption> = ArrayList(),
        val patchFile: String? = null
    ) : FlashParams()

    @Serializable
    data class Module(val file: String) : FlashParams()

    @Serializable
    object Uninstall : FlashParams()

}
