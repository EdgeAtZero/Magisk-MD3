package com.topjohnwu.magisk.ui.superuser

import com.topjohnwu.magisk.ui.superuser.AppPriority.Deny
import com.topjohnwu.magisk.ui.superuser.AppPriority.SuperUser
import kotlinx.serialization.Serializable

@Serializable
sealed class AppPriority {

    abstract fun compare(p0: AppInfo, p1: AppInfo): Int

    @Serializable
    object SuperUser : AppPriority() {
        override fun compare(p0: AppInfo, p1: AppInfo): Int =
            p1.isSuperUserActive.compareTo(p0.isSuperUserActive)
    }

    @Serializable
    object Deny : AppPriority() {
        override fun compare(p0: AppInfo, p1: AppInfo): Int =
            p1.isDeny.compareTo(p0.isDeny)
    }

}

val AppPriorities = listOf(SuperUser, Deny)
