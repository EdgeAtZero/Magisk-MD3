package com.topjohnwu.magisk.ui.home

import android.os.Build
import android.system.Os
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.ui.icon.Linux
import com.topjohnwu.magisk.ui.icon.Magisk
import com.topjohnwu.magisk.util.getSELinuxStatus
import com.topjohnwu.magisk.util.getZygiskInfo
import me.edgeatzero.compose.component.Card
import me.edgeatzero.compose.component.ListItem


@Composable
fun InfoCard(modifier: Modifier = Modifier, autoExpand: Boolean = false) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(autoExpand) {
        if (autoExpand) {
            expanded = true
        }
    }
    Card(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            ListItem(
                headlineContent = { Text(text = "管理器版本") },
                supportingContent = { Text(text = "${BuildConfig.APP_VERSION_NAME} (${BuildConfig.APP_VERSION_CODE})" + if (BuildConfig.DEBUG) " (D)" else "") },
                leadingContent = { Icon(imageVector = Icons.Filled.Magisk, contentDescription = null) }
            )
            if (Info.isZygiskEnabled) {
                ListItem(
                    headlineContent = { Text(text = "Zygisk 状态") },
                    supportingContent = { Text(text = "已启用" + getZygiskInfo()?.let { " | ${it.name} | ${it.version}" }) },
                    leadingContent = { Icon(imageVector = Icons.Filled.Vaccines, contentDescription = null) }
                )
            }
            if (!expanded) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = null)
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    val uname = Os.uname()
                    ListItem(
                        headlineContent = { Text(text = "内核版本") },
                        supportingContent = { Text(text = "${uname.release} (${uname.machine})") },
                        leadingContent = { Icon(imageVector = Icons.Filled.Linux, contentDescription = null) }
                    )
                    ListItem(
                        headlineContent = { Text(text = "Android 版本") },
                        supportingContent = { Text(text = "${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})") },
                        leadingContent = { Icon(imageVector = Icons.Filled.Android, contentDescription = null) }
                    )
                    ListItem(
                        headlineContent = { Text(text = "ABI 类型") },
                        supportingContent = { Text(text = Build.SUPPORTED_ABIS.joinToString(", ")) },
                        leadingContent = { Icon(imageVector = Icons.Filled.Memory, contentDescription = null) }
                    )
                    if (Info.env.isActive) {
                        ListItem(
                            headlineContent = { Text(text = "SELinux状态") },
                            supportingContent = { Text(text = getSELinuxStatus()) },
                            leadingContent = { Icon(imageVector = Icons.Filled.Security, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}
