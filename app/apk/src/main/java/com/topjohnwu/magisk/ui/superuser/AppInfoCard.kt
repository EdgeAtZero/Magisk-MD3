package com.topjohnwu.magisk.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipScope
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.topjohnwu.magisk.ui.superuser.AppInfo
import com.topjohnwu.magisk.ui.superuser.SuperUserViewModel
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Composable
fun AppInfoCard(
    modifier: Modifier = Modifier,
    item: AppInfo,
    viewModel: SuperUserViewModel? = null,
    isQuickSettingsEnable: Boolean = true,
    isSelected: Boolean = false,
    isShowLabel: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .let {
                if (onClick != null) it.clickable(onClick = onClick, role = Role.Button) else it
            }
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp) else Color.Unspecified
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                modifier = Modifier.size(38.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.packageInfo)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                if (item.label == item.packageName) {
                    Text(
                        text = item.label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = item.label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = item.packageName,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                if (isShowLabel) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .animateContentSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (item.isSystemApp) {
                            TextLabel("SYSTEM")
                        }
                        if (item.isSharedUID) {
                            TextLabel("SharedUID")
                        }
                        AnimatedVisibility(item.isSuperUserActive) {
                            AnimatedContent(targetState = item.isSuperUser) {
                                TextLabel(if (it) "SU" else "SU REJECT")
                            }
                        }
                        AnimatedVisibility(item.isDeny) {
                            TextLabel("DENY")
                        }
                    }
                }
            }
            if (isQuickSettingsEnable && viewModel != null) {
                val coroutineScope = rememberCoroutineScope()
                val tooltipState = rememberTooltipState()
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    state = tooltipState,
                    hasAction = false,
                    tooltip = { AppInfoCardTooltip(item, viewModel) }
                ) {
                    IconButton(onClick = { coroutineScope.launch { tooltipState.show(MutatePriority.PreventUserInput) } }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun TooltipScope.AppInfoCardTooltip(item: AppInfo, viewModel: SuperUserViewModel) {
    RichTooltip(
        caretShape = TooltipDefaults.caretShape()
    ) {
        Column(
            modifier = Modifier
                .width(200.dp)
        ) {
            @Composable
            fun Item(
                icon: ImageVector,
                text: String,
                checked: Boolean,
                onCheckedChange: (Boolean) -> Unit
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier.clickable(
                        onClick = { onCheckedChange(!checked) },
                        role = Role.Button,
                        interactionSource = interactionSource,
                        indication = null
                    ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
                        imageVector = icon,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = text
                    )
                    Switch(
                        checked = checked,
                        onCheckedChange = null,
                        interactionSource = interactionSource
                    )
                }
            }
            AnimatedVisibility(item.isSuperUserActive) {
                Column {
                    Item(
                        icon = Icons.Filled.BugReport,
                        text = "日志",
                        checked = item.isLogging,
                        onCheckedChange = { viewModel.updateLogging(item, it) }
                    )
                    Item(
                        icon = Icons.Filled.Notifications,
                        text = "通知",
                        checked = item.isNotify,
                        onCheckedChange = { viewModel.updateNotify(item, it) }
                    )
                }
            }
            Item(
                icon = Icons.Filled.SearchOff,
                text = "排除列表",
                checked = item.isDeny,
                onCheckedChange = { viewModel.updateDeny(item, it) }
            )
            Item(
                icon = Icons.Filled.AdminPanelSettings,
                text = "超级用户",
                checked = item.isSuperUser,
                onCheckedChange = { viewModel.updateSuperUser(item, it) }
            )
            AnimatedVisibility(item.isSuperUserActive) {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.deleteSuperUser(item) }
                ) {
                    Text(
                        text = "撤销超级用户",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
