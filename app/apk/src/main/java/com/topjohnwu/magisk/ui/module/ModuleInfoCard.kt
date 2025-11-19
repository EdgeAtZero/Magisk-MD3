package com.topjohnwu.magisk.ui.module

import android.text.format.Formatter
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.ui.component.TextLabel
import me.edgeatzero.compose.component.Card
import me.edgeatzero.compose.component.Switch
import me.edgeatzero.compose.theme.MaterialColors

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ModuleInfoCard(
    modifier: Modifier = Modifier,
    item: ModuleInfo,
    isShowId: Boolean = false,
    onAction: () -> Unit,
    onUpdate: (enable: Boolean?, remove: Boolean?) -> Unit,
    onUpdateRequest: () -> Unit,
    onWeb: () -> Unit
) {
    val context = LocalContext.current
    val isCardEnabled = item.isEnable && !item.isRemove && item.notice == null
    val isSwitchEnabled = !item.isRemove
    val interactionSource = remember { MutableInteractionSource() }
    val textDecoration = TextDecoration.LineThrough.takeIf { item.isRemove }

    Card(
        modifier = modifier.clickable(
            enabled = isSwitchEnabled,
            indication = LocalIndication.current,
            interactionSource = interactionSource,
            onClick = { onUpdate(!item.isEnable, null) }
        ),
        enabled = isCardEnabled,
        interactionSource = interactionSource
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
                Row(horizontalArrangement = Arrangement.SpaceAround) {
                    Column(modifier = Modifier.weight(1f)) {
                        FlowRow(
                            modifier = Modifier.padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AnimatedVisibility(
                                visible = isShowId,
                                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
                            ) {
                                TextLabel(text = item.id)
                            }
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
                        enabled = isSwitchEnabled,
                        interactionSource = interactionSource,
                        onCheckedChange = null
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
                                FilledTonalIconButton(shapes = IconButtonDefaults.shapes(), onClick = onAction) {
                                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                                }
                            }
                            AnimatedVisibility(visible = item.isWeb) {
                                FilledTonalIconButton(shapes = IconButtonDefaults.shapes(), onClick = onWeb) {
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
                            FilledTonalIconButton(shapes = IconButtonDefaults.shapes(), onClick = onUpdateRequest) {
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
