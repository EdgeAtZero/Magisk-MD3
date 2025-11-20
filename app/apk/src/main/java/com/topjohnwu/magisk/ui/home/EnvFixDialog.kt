package com.topjohnwu.magisk.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.core.ktx.reboot
import com.topjohnwu.magisk.core.tasks.MagiskInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.edgeatzero.android.setupWindowBlurListener
import me.edgeatzero.compose.component.SplicedCard
import me.edgeatzero.compose.util.rememberToastAction

@Composable
fun EnvFixDialog(
    modifier: Modifier = Modifier,
    code: Int,
    onDismissRequest: () -> Unit,
    onNavigateToInstaller: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val rebootToast = rememberToastAction(message = "%s", duration = Toast.LENGTH_LONG)
    var isFixing by rememberSaveable { mutableStateOf(false) }
    when {
        code == 2 || // No rules block, module policy not loaded
                Info.env.versionCode != BuildConfig.APP_VERSION_CODE ||
                Info.env.versionString != BuildConfig.APP_VERSION_NAME -> AlertDialog(
            modifier = modifier,
            icon = {
                Icon(imageVector = Icons.Filled.QuestionMark, contentDescription = null)
            },
            title = {
                Text(text = "需要修复运行环境")
            },
            text = {
                Text(text = "需要重新安装才能使 Magisk 正常工作。请在应用内重新安装，recovery 模式无法正确获取设备信息。")
                (LocalView.current.parent as DialogWindowProvider).setupWindowBlurListener()
            },
            confirmButton = {
                TextButton(onClick = { onDismissRequest(); onNavigateToInstaller() }) { Text(text = "确定") }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text(text = "取消") }
            },
            onDismissRequest = onDismissRequest
        )

        isFixing -> AlertDialog(
            modifier = modifier,
            icon = {
                Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
            },
            title = {
                Text(text = "修复安装")
            },
            text = {
                Text(text = "正在修复运行环境")
                (LocalView.current.parent as DialogWindowProvider).setupWindowBlurListener()
            },
            confirmButton = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            onDismissRequest = onDismissRequest
        )

        else -> AlertDialog(
            modifier = modifier,
            icon = {
                Icon(imageVector = Icons.Filled.QuestionMark, contentDescription = null)
            },
            title = {
                Text(text = "需要修复运行环境")
            },
            text = {
                Text(text = "需要一些额外的安装才能使 Magisk 正常工作。完成后自动重启，是否继续？")
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
                                onClick = {
                                    coroutineScope.launch {
                                        isFixing = true
                                        MagiskInstaller.FixEnv().exec { success ->
                                            onDismissRequest()
                                            rebootToast(if (success) "设备将在 5 秒后重启" else "安装失败")
                                            if (success) {
                                                coroutineScope.launch(Dispatchers.Default) {
                                                    delay(5000)
                                                    reboot()
                                                }
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text(text = "确定")
                            }
                        },
                        {
                            TextButton(
                                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                                shape = RectangleShape,
                                onClick = onDismissRequest
                            ) {
                                Text(text = "取消")
                            }
                        }
                    )
                )
            },
            onDismissRequest = onDismissRequest
        )
    }
}
