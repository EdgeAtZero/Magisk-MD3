package com.topjohnwu.magisk.ui.module

import com.topjohnwu.magisk.ui.module.ModuleSort.*
import kotlinx.serialization.Serializable

@Serializable
sealed class ModuleSort() {

    abstract fun compare(p0: ModuleInfo, p1: ModuleInfo): Int

    @Serializable
    object ID : ModuleSort() {
        override fun compare(p0: ModuleInfo, p1: ModuleInfo): Int =
            p0.id.compareTo(p1.id)
    }

    @Serializable
    object Name : ModuleSort() {
        override fun compare(p0: ModuleInfo, p1: ModuleInfo): Int =
            p0.name.compareTo(p1.name)
    }

    @Serializable
    object Size : ModuleSort() {
        override fun compare(p0: ModuleInfo, p1: ModuleInfo): Int =
            p1.size.compareTo(p0.size)
    }

}

val ModuleSorts = listOf(ID, Name, Size)
