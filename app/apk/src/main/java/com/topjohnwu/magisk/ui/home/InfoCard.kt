package com.topjohnwu.magisk.ui.component

import android.os.Build
import android.system.Os
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.icon.Linux
import com.topjohnwu.magisk.ui.icon.Magisk
import com.topjohnwu.magisk.util.getSELinuxStatus
import com.topjohnwu.magisk.util.getZygiskImplementation
import com.topjohnwu.magisk.util.getZygiskVersion


@Composable
fun InfoCard(modifier: Modifier = Modifier, autoExpand: Boolean = false) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(autoExpand) {
        if (autoExpand) {
            expanded = true
        }
    }
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 24.dp)
        ) {
            InfoCardItem(
                label = "管理器版本",
                icon = Icons.Filled.Magisk,
                content = "${BuildConfig.APP_VERSION_NAME} (${BuildConfig.APP_VERSION_CODE})" +
                        if (BuildConfig.DEBUG) " (D)" else ""
            )
            val zygiskImplementation = getZygiskImplementation()
            if (zygiskImplementation.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                InfoCardItem(
                    label = "Zygisk 状态",
                    content = "已启用 | $zygiskImplementation | ${getZygiskVersion()}",
                    icon = Icons.Filled.Vaccines
                )
            }
            if (!expanded) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Show more"
                        )
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    val uname = Os.uname()
                    Spacer(Modifier.height(16.dp))
                    InfoCardItem(
                        label = "内核版本",
                        content = "${uname.release} (${uname.machine})",
                        icon = Icons.Filled.Linux,
                    )
                    Spacer(Modifier.height(16.dp))
                    InfoCardItem(
                        label = "Android 版本",
                        content = "${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})",
                        icon = Icons.Filled.Android,
                    )
                    Spacer(Modifier.height(16.dp))
                    InfoCardItem(
                        label = "ABI 类型",
                        content = Build.SUPPORTED_ABIS.joinToString(", "),
                        icon = Icons.Filled.Memory,
                    )
                    if (Info.env.isActive) {
                        Spacer(Modifier.height(16.dp))
                        InfoCardItem(
                            label = "SELinux状态",
                            content = getSELinuxStatus(),
                            icon = Icons.Filled.Security,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCardItem(label: String, content: String, icon: Any? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 20.dp)
                )

                is Painter -> Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
