package com.topjohnwu.magisk.ui.superuser

import com.topjohnwu.magisk.ui.superuser.AppSort.*
import kotlinx.serialization.Serializable

@Serializable
sealed class AppSort() {

    abstract fun compare(p0: AppInfo, p1: AppInfo): Int

    @Serializable
    object Name : AppSort() {
        override fun compare(p0: AppInfo, p1: AppInfo): Int =
            p0.label.compareTo(p1.label)
    }

    @Serializable
    object Package : AppSort() {
        override fun compare(p0: AppInfo, p1: AppInfo): Int =
            p0.packageName.compareTo(p1.packageName)
    }

    @Serializable
    object UID : AppSort() {
        override fun compare(p0: AppInfo, p1: AppInfo): Int =
            p0.uid.compareTo(p1.uid)
    }

}

val AppSorts = listOf(Name, Package, UID)
