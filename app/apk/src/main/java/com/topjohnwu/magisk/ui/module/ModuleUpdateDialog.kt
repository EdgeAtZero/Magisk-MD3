package com.topjohnwu.magisk.ui.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SimCardDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import me.edgeatzero.android.setupWindowBlurListener

@Composable
fun ModuleUpdateDialog(
    modifier: Modifier = Modifier,
    item: ModuleInfo,
    onDismissRequest: () -> Unit,
    onDownload: (Boolean) -> Unit
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(imageVector = Icons.Filled.SimCardDownload, contentDescription = null)
        },
        title = {
            Text(text = "安装 ${item.name} ${item.version} (${item.versionCode})")
        },
        text = {
            ModuleUpdateMarkdown(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                markdown = item.updateInfo
            )
            (LocalView.current.parent as DialogWindowProvider).setupWindowBlurListener()
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Box(modifier = Modifier.weight(1f)) {
                    TextButton(onClick = onDismissRequest) { Text(text = "关闭") }
                }
                TextButton(onClick = { onDismissRequest(); onDownload(false) }) { Text(text = "下载") }
                TextButton(onClick = { onDismissRequest(); onDownload(true) }) { Text(text = "安装") }
            }
        },
        onDismissRequest = onDismissRequest
    )
}
