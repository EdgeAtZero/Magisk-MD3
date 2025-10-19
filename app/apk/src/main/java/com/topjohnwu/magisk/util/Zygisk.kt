package com.topjohnwu.magisk.ui.util

import com.topjohnwu.superuser.ShellUtils

fun getZygiskImplementation(): String {
    val modulesPath = "/data/adb/modules"
    val zygiskModuleIds = arrayOf(
        "rezygisk",
        "zygisksu"
    )
    return try {
        zygiskModuleIds.firstNotNullOfOrNull { moduleName ->
            val modulePath = "$modulesPath/$moduleName"
            val isEnabled = ShellUtils.fastCmdResult("test -f $modulePath/module.prop && test ! -f $modulePath/disable")
            if (!isEnabled) return@firstNotNullOfOrNull null
            ShellUtils.fastCmd("grep '^name=' $modulePath/module.prop | cut -d'=' -f2").takeIf { it.isNotBlank() }
        } ?: ""
    } catch (_: Exception) {
        ""
    }
}

fun getZygiskVersion(): String {
    val modulesPath = "/data/adb/modules"
    val zygiskModuleIds = arrayOf(
        "rezygisk",
        "zygisksu"
    )
    return try {
        zygiskModuleIds.firstNotNullOfOrNull { moduleName ->
            val modulePath = "$modulesPath/$moduleName"
            val isEnabled = ShellUtils.fastCmdResult("test -f $modulePath/module.prop && test ! -f $modulePath/disable")
            if (!isEnabled) return@firstNotNullOfOrNull null
            ShellUtils.fastCmd("grep '^version=' $modulePath/module.prop | cut -d'=' -f2").takeIf { it.isNotBlank() }
        } ?: "None"
    } catch (_: Exception) {
        "None"
    }
}
