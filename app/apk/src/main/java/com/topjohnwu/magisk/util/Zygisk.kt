package com.topjohnwu.magisk.util

import com.topjohnwu.superuser.ShellUtils
import kotlinx.serialization.Serializable

@Serializable
data class ZygiskInfo(
    val name: String,
    val version: String
)

fun getZygiskInfo(): ZygiskInfo? =
    runCatching {
        val modulesPath = "/data/adb/modules"
        val zygiskModuleIds = arrayOf("rezygisk", "zygisksu")
        zygiskModuleIds.firstNotNullOfOrNull block@{ moduleName ->
            val modulePath = "$modulesPath/$moduleName"
            val isEnabled = ShellUtils.fastCmdResult("test -f $modulePath/module.prop && test ! -f $modulePath/disable")
            isEnabled || return@block null
            ZygiskInfo(
                name = ShellUtils.fastCmd("grep '^name=' $modulePath/module.prop | cut -d'=' -f2")
                    .takeIf { it.isNotBlank() } ?: return@block null,
                version = ShellUtils.fastCmd("grep '^version=' $modulePath/module.prop | cut -d'=' -f2 | cut -d'(' -f1")
                    .trim()
                    .takeIf { it.isNotBlank() } ?: return@block null
            )
        }
    }.getOrNull()
