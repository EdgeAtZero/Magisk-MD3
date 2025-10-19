package com.topjohnwu.magisk.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.Info


@Composable
fun StatusCard(modifier: Modifier = Modifier, onNavigateToInstall: () -> Unit = {}) {
    if (Info.env.isActive) {
        ElevatedCard(
            modifier = modifier,
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (Info.env.isActive) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null
                )
                Column(Modifier.padding(start = 20.dp)) {
                    Text(text = "工作中")
                    Text(text = "版本: ${Info.env.versionString} (${Info.env.versionCode})")
                }
            }
        }
    } else {
        ElevatedCard(
            modifier = modifier,
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (Info.env.isActive) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            ),
            onClick = onNavigateToInstall
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = null
                )
                Column(Modifier.padding(start = 20.dp)) {
                    Text(text = "未安装 Magisk")
                    Text(text = "请点击以继续")
                }
            }
        }
    }
}
