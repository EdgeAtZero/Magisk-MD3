package com.topjohnwu.magisk.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.topjohnwu.magisk.core.Info
import me.edgeatzero.compose.component.Card
import me.edgeatzero.compose.component.ListItem

@Composable
fun StatusCard(modifier: Modifier = Modifier, onNavigateToInstall: () -> Unit = {}) {
    if (Info.env.isActive) {
        Card(
            modifier = modifier,
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (Info.env.isActive) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            ListItem(
                headlineContent = { Text(text = "工作中") },
                supportingContent = { Text(text = "版本: ${Info.env.versionString} (${Info.env.versionCode})" + if (Info.env.isDebug) " (D)" else "") },
                leadingContent = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null) }
            )
        }
    } else {
        Card(
            modifier = modifier,
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (Info.env.isActive) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            ),
            shape = MaterialTheme.shapes.medium,
            onClick = onNavigateToInstall
        ) {
            ListItem(
                headlineContent = { Text(text = "未安装 Magisk") },
                supportingContent = { Text(text = "请点击以继续") },
                leadingContent = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null) }
            )
        }
    }
}
