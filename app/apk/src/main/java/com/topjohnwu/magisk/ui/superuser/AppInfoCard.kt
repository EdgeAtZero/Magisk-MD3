package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.topjohnwu.magisk.ui.component.TextLabel
import me.edgeatzero.compose.component.Card
import me.edgeatzero.compose.component.ListExpandableItem


@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun AppInfoCard(
    modifier: Modifier = Modifier,
    item: AppInfo,
    subtext: (AppInfo) -> String,
    viewModel: SuperUserViewModel? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(modifier = modifier) {
        ListExpandableItem(
            expanded = isExpanded,
            onClick = { isExpanded = !isExpanded },
            headlineContent = {
                Text(
                    text = item.label,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Column {
                    if (item.label != item.packageName) {
                        Text(
                            text = subtext(item),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    FlowRow(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .animateContentSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (item.isSystemApp) {
                            TextLabel(text = "SYSTEM")
                        }
                        if (item.isSharedUID) {
                            TextLabel(text = "SharedUID")
                        }
                        AnimatedVisibility(visible = item.isSuperUserActive) {
                            AnimatedContent(targetState = item.isSuperUser) {
                                if (it) {
                                    TextLabel(
                                        text = "SU",
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                } else {
                                    TextLabel(text = "SU REJECT")
                                }
                            }
                        }
                        AnimatedVisibility(visible = item.isDeny) {
                            TextLabel(text = "DENY")
                        }
                    }
                }
            },
            overlineContent = {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (item.isDenyAvailable) FilterChip(
                        selected = item.isDeny,
                        onClick = { viewModel?.updateDenyPolicy(item, enable = !item.isDeny) },
                        label = { Text(text = "排除列表") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.SearchOff, contentDescription = null) }
                    )
                    FilterChip(
                        selected = item.isSuperUser,
                        onClick = { viewModel?.updateSuPolicy(item, enable = !item.isSuperUser) },
                        label = { Text(text = "超级用户") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.AdminPanelSettings, contentDescription = null) }
                    )
                    AnimatedVisibility(visible = item.isSuperUserActive) {
                        FilterChip(
                            selected = item.isLogging,
                            onClick = { viewModel?.updateSuPolicy(item, logging = !item.isLogging) },
                            label = { Text(text = "日志") },
                            leadingIcon = { Icon(imageVector = Icons.Filled.BugReport, contentDescription = null) }
                        )
                    }
                    AnimatedVisibility(visible = item.isSuperUserActive) {
                        FilterChip(
                            selected = item.isNotify,
                            onClick = { viewModel?.updateSuPolicy(item, notification = !item.isNotify) },
                            label = { Text(text = "通知") },
                            leadingIcon = { Icon(imageVector = Icons.Filled.Notifications, contentDescription = null) }
                        )
                    }
                    AnimatedVisibility(visible = item.isSuperUserActive) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel?.updateSuPolicy(item) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.errorContainer),
                            label = { Text(text = "撤销超级用户") }
                        )
                    }
                }
            },
            leadingContent = {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.packageInfo)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                )
            }
        )
    }
}
