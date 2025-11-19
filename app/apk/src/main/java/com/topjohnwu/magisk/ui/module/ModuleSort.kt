package com.topjohnwu.magisk.ui.module

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.ui.graphics.vector.ImageVector
import com.topjohnwu.magisk.ui.module.ModuleSort.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
