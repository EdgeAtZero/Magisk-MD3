package com.topjohnwu.magisk.ui.flash

import com.topjohnwu.magisk.ui.flash.FlashAction.*
import com.topjohnwu.magisk.ui.flash.FlashAction.Install
import com.topjohnwu.magisk.ui.install.InstallMethod
import com.topjohnwu.magisk.ui.install.InstallOption
import com.topjohnwu.magisk.ui.navigation.MainDestination
import kotlinx.serialization.Serializable

@Serializable
sealed class FlashAction {

    @Serializable
    data class Install(
        val method: InstallMethod,
        val options: ArrayList<InstallOption> = ArrayList(),
        val patchFile: String? = null
    ) : FlashAction() {
        constructor(dest: MainDestination.HomeNav.Flash) : this(dest.method, dest.options, dest.patchFile)
    }

    @Serializable
    data class Module(val file: String) : FlashAction() {
        constructor(dest: MainDestination.ModuleNav.Install) : this(dest.uri)
    }

    @Serializable
    object Uninstall : FlashAction()

}

val FlashActions = listOf(Install, Module, Uninstall)
