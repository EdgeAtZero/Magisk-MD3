package com.topjohnwu.magisk.ui.util

import androidx.compose.runtime.Composable
import com.topjohnwu.superuser.io.SuFile


@Composable
fun getSELinuxStatus() = SuFile("/sys/fs/selinux/enforce").run {
    when {
        !exists() -> "禁用"
        !isFile -> "未知"
        !canRead() -> "严格模式"
        else -> when (runCatching { newInputStream() }.getOrNull()?.bufferedReader()
            ?.use { it.runCatching { readLine() }.getOrNull()?.trim()?.toIntOrNull() }) {
            1 -> "严格模式"
            0 -> "宽容模式"
            else -> "未知"
        }
    }
}
