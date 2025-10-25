package com.topjohnwu.magisk.ui.install

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import me.edgeatzero.android.setupWindowBlurListener

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
            TextButton(onClick = onDismissRequest) {
                Text(text = "确定")
            }
        },
        onDismissRequest = onDismissRequest
    )
}
