package com.topjohnwu.magisk.ui.surequest

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.topjohnwu.magisk.core.ktx.getLabel
import com.topjohnwu.magisk.core.model.su.SuPolicy
import me.edgeatzero.android.setupWindowBlurListener
import me.edgeatzero.compose.util.rememberToastAction

private const val SHARED_ID_PREFIX = "[SharedUID] "

fun SuAllowTimeout.text() =
    when (this) {
        SuAllowTimeout.Forever -> "永远"
        SuAllowTimeout.Once -> "仅此一次"
        SuAllowTimeout.TenMin -> "10分钟"
        SuAllowTimeout.TwentyMin -> "20分钟"
        SuAllowTimeout.ThirtyMin -> "30分钟"
        SuAllowTimeout.SixtyMin -> "60分钟"
    }

@Composable
fun SuRequestDialog(modifier: Modifier = Modifier, viewModel: SuRequestViewModel) {
    val ai = remember(viewModel) { viewModel.handler.pkgInfo.applicationInfo }
    val pi = remember(viewModel) { viewModel.handler.pkgInfo }
    val pm = remember(viewModel) { viewModel.handler.pm }
    val seconds by viewModel.seconds.collectAsState()
    val selected by viewModel.selected.collectAsState()
    rememberToastAction(
        message = "由于某个应用遮挡了超级用户请求界面，因此 Magisk 无法验证您的回应",
        duration = Toast.LENGTH_SHORT
    )
    val isGrantEnabled by viewModel.isGrantEnabled.collectAsState()
    AlertDialog(
        modifier = modifier,
        icon = {
            AsyncImage(
                modifier = Modifier.size(54.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pi)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
            )
        },
        title = {
            val packageName: String
            val title: String
            if (ai != null) {
                packageName = ai.packageName
                title = if (pi.sharedUserId != null) SHARED_ID_PREFIX else "" + ai.getLabel(pm)
            } else {
                packageName = pi.sharedUserId.toString()
                title = SHARED_ID_PREFIX + pi.sharedUserId
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    SuAllowTimeout.entries.forEachIndexed { index, entry ->
                        SegmentedButton(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            selected = selected == index,
                            shape = SegmentedButtonDefaults.itemShape(index, SuAllowTimeout.entries.size),
                            onClick = { viewModel.selected.value = index }
                        ) {
                            Text(text = entry.text())
                        }
                    }
                }
                Text(text = "将授予对该设备的最高权限。\n如果不确定，请拒绝！", textAlign = TextAlign.Center)
            }
            (LocalView.current.parent as DialogWindowProvider).setupWindowBlurListener()
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                TextButton(onClick = { viewModel.respond(SuPolicy.DENY) }) {
                    Text(text = "拒绝" + if (seconds > 0) " (${seconds}s)" else "")
                }
                TextButton(enabled = isGrantEnabled, onClick = { viewModel.respond(SuPolicy.ALLOW) }) {
                    Text(text = "允许")
                }
            }
        },
        onDismissRequest = { viewModel.respond(SuPolicy.DENY) }
    )
}
