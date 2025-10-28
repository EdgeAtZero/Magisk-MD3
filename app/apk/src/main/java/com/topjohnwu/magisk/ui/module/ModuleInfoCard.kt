package com.topjohnwu.magisk.ui.module

import android.text.format.Formatter
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Wysiwyg
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.ui.component.TextLabel
import me.edgeatzero.compose.theme.MaterialColors

@Composable
fun ModuleInfoCard(
    modifier: Modifier = Modifier,
    item: ModuleInfo,
    onAction: () -> Unit,
    onUpdate: (enable: Boolean?, remove: Boolean?) -> Unit,
    onUpdateRequest: () -> Unit,
    onWeb: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = modifier,
        enabled = item.isEnable && !item.isRemove && item.notice == null,
        colors = CardDefaults.elevatedCardColors().let { it.copy(disabledContainerColor = it.containerColor) },
        shape = MaterialTheme.shapes.medium,
        onClick = { onUpdate(false, null) }
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Crossfade(
                modifier = Modifier.align(Alignment.Center),
                targetState = when {
                    item.isRemove -> Icons.Default.Delete
                    item.isUpdated -> Icons.Default.SystemUpdate
                    else -> null
                }
            ) { targetState ->
                targetState?.let { icon ->
                    Image(
                        modifier = Modifier.size(100.dp),
                        imageVector = icon,
                        colorFilter = ColorFilter.tint(MaterialColors.Gray[400].copy(alpha = 0.5f)),
                        contentDescription = null
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val textDecoration = TextDecoration.LineThrough.takeIf { item.isRemove }

                Row(horizontalArrangement = Arrangement.SpaceAround) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val sizeText = remember(context, item.size) { Formatter.formatFileSize(context, item.size) }
                            TextLabel(text = sizeText)
                            if (item.isActionable) {
                                TextLabel(text = "可执行")
                            }
                            if (item.isWeb) {
                                TextLabel(text = "WEB")
                            }
                            AnimatedVisibility(
                                visible = item.isOutdated != null,
                                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
                            ) {
                                TextLabel(text = if (item.isOutdated == true) "可更新" else "已最新")
                            }
                        }
                        Crossfade(targetState = textDecoration) { targetState ->
                            Column {
                                Text(
                                    text = item.name,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = targetState
                                )
                                Text(
                                    text = "版本: ${item.version}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall,
                                    textDecoration = targetState
                                )
                                Text(
                                    text = "作者: ${item.author}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall,
                                    textDecoration = targetState
                                )
                            }
                        }
                    }
                    Switch(
                        modifier = Modifier.padding(start = 16.dp),
                        checked = item.isEnable,
                        enabled = !item.isRemove,
                        onCheckedChange = { onUpdate(!item.isEnable, null) }
                    )
                }
                Crossfade(targetState = textDecoration) { targetState ->
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        textDecoration = targetState
                    )
                }
                item.notice?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceAround) {
                    Row(modifier = Modifier.weight(1f)) {
                        if (item.notice == null) {
                            AnimatedVisibility(visible = item.isActionable) {
                                FilledTonalIconButton(onClick = onAction) {
                                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                                }
                            }
                            AnimatedVisibility(visible = item.isWeb) {
                                FilledTonalIconButton(onClick = onWeb) {
                                    Icon(imageVector = Icons.AutoMirrored.Outlined.Wysiwyg, contentDescription = null)
                                }
                            }
                        }
                    }
                    Row {
                        AnimatedVisibility(
                            visible = item.isOutdated == true,
                            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                        ) {
                            FilledTonalIconButton(onClick = onUpdateRequest) {
                                Icon(imageVector = Icons.Filled.Download, contentDescription = null)
                            }
                        }
                        Crossfade(targetState = item.isRemove) { targetState ->
                            FilledTonalButton(
                                enabled = !item.isUpdated,
                                colors = ButtonDefaults.filledTonalButtonColors().let {
                                    it.copy(
                                        containerColor = if (targetState) it.containerColor else MaterialTheme.colorScheme.errorContainer,
                                        contentColor = if (targetState) it.contentColor else MaterialTheme.colorScheme.onErrorContainer
                                    )
                                },
                                onClick = { onUpdate(null, !item.isRemove) }
                            ) {
                                if (targetState) {
                                    Icon(imageVector = Icons.Filled.Restore, contentDescription = null)
                                    Text(text = "恢复")
                                } else {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                                    Text(text = "删除")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
