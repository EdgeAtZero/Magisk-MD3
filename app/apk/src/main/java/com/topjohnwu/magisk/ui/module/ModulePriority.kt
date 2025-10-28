package com.topjohnwu.magisk.ui.module

import com.topjohnwu.magisk.ui.module.ModulePriority.*
import kotlinx.serialization.Serializable

@Serializable
sealed class ModulePriority {

    abstract fun compare(p0: ModuleInfo, p1: ModuleInfo): Int

    @Serializable
    object Acton : ModulePriority() {
        override fun compare(p0: ModuleInfo, p1: ModuleInfo): Int =
            p1.isActionable.compareTo(p0.isActionable)
    }

    @Serializable
    object Update : ModulePriority() {
        override fun compare(p0: ModuleInfo, p1: ModuleInfo): Int =
            (p1.isOutdated ?: false).compareTo(p0.isOutdated ?: false)
    }

    @Serializable
    object Web : ModulePriority() {
        override fun compare(p0: ModuleInfo, p1: ModuleInfo): Int =
            p1.isWeb.compareTo(p0.isWeb)
    }

}

val ModulePriorities = listOf(Acton, Update, Web)
