package com.topjohnwu.magisk.ui.install

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import me.edgeatzero.android.setupWindowBlurListener
import me.edgeatzero.compose.component.SplicedCard

@ExperimentalMaterial3Api
@Composable
fun InactiveSlotWarningDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
        },
        title = {
            Text(text = "注意")
        },
        text = {
            Text(text = "将在重启后强制切换到另一个槽位！注意只能在 OTA 更新完成后的重启之前使用。")
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
                            onClick = onDismissRequest
                        ) {
                            Text(text = "确定")
                        }
                    }
                )
            )
        },
        onDismissRequest = onDismissRequest
    )
}
