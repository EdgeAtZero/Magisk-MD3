package com.topjohnwu.magisk.ui.superuser

import com.topjohnwu.magisk.ui.superuser.AppSort.*
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1

typealias AppSortData = Pair<AppSort<*>, Order>

@Serializable
sealed class AppSort<T : Comparable<T>>() {

    protected abstract val kProperty0: KProperty1<AppInfo, T>

    fun equals(info0: AppInfo, info1: AppInfo): Boolean =
        kProperty0.get(info0) == kProperty0.get(info1)

    fun compare(info0: AppInfo, info1: AppInfo): Int =
        kProperty0.get(info0).compareTo(kProperty0.get(info1))

    open val isNeedOrder: Boolean = true

    @Serializable
    object AppName : AppSort<String>() {
        override val kProperty0 = AppInfo::label
    }

    @Serializable
    object PackageName : AppSort<String>() {
        override val kProperty0 = AppInfo::packageName
    }

    @Serializable
    object UID : AppSort<Int>() {
        override val kProperty0 = AppInfo::uid
    }

    @Serializable
    object WhetherDeny : AppSort<Boolean>() {
        override val kProperty0 = AppInfo::isDeny
        override val isNeedOrder = false
    }

    @Serializable
    object WhetherSU : AppSort<Boolean>() {
        override val kProperty0 = AppInfo::isSuperUserActive
        override val isNeedOrder = false
    }

    @Serializable
    enum class Order {

        ASC, DESC;

        operator fun not() = if (this == ASC) DESC else ASC

    }

}

val AppSorts = arrayOf(WhetherSU, WhetherDeny, AppName, PackageName, UID)
