package com.topjohnwu.magisk.ui.module

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SimCardDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import me.edgeatzero.android.setupWindowBlurListener
import me.edgeatzero.compose.component.SplicedCard

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
            SplicedCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                content = listOf(
                    {
                        TextButton(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                            shape = RectangleShape,
                            onClick = { onDismissRequest(); onDownload(true) }
                        ) {
                            Text(text = "安装")
                        }
                    },
                    {
                        TextButton(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                            shape = RectangleShape,
                            onClick = { onDismissRequest(); onDownload(false) }
                        ) {
                            Text(text = "下载")
                        }
                    },
                    {
                        TextButton(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                            shape = RectangleShape,
                            onClick = onDismissRequest
                        ) {
                            Text(text = "关闭")
                        }
                    }
                )
            )
        },
        onDismissRequest = onDismissRequest
    )
}
