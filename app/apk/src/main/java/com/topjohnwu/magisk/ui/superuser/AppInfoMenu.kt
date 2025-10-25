package com.topjohnwu.magisk.ui.superuser

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoMenu(modifier: Modifier = Modifier, item: AppInfo, viewModel: SuperUserViewModel) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        IconButton(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            onClick = {},
        ) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
        }
        ExposedDropdownMenu(
            expanded = isExpanded,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            matchAnchorWidth = false,
            onDismissRequest = { isExpanded = false },
        ) {
            @Composable
            fun DropdownMenuItem(
                icon: ImageVector,
                text: String,
                checked: Boolean,
                onCheckedChange: (Boolean) -> Unit
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                DropdownMenuItem(
                    leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
                    text = { Text(text = text) },
                    trailingIcon = {
                        Switch(checked = checked, onCheckedChange = null, interactionSource = interactionSource)
                    },
                    interactionSource = interactionSource,
                    onClick = { isExpanded = false; onCheckedChange(!checked) }
                )
            }
            if (item.isSuperUserActive) {
                DropdownMenuItem(
                    icon = Icons.Filled.BugReport,
                    text = "日志",
                    checked = item.isLogging,
                    onCheckedChange = { isExpanded = false; viewModel.updateSuPolicy(item, logging = it) }
                )
                DropdownMenuItem(
                    icon = Icons.Filled.Notifications,
                    text = "通知",
                    checked = item.isNotify,
                    onCheckedChange = { isExpanded = false; viewModel.updateSuPolicy(item, notification = it) }
                )
            }
            DropdownMenuItem(
                icon = Icons.Filled.SearchOff,
                text = "排除列表",
                checked = item.isDeny,
                onCheckedChange = { isExpanded = false; viewModel.updateDenyPolicy(item, enable = it) }
            )
            DropdownMenuItem(
                icon = Icons.Filled.AdminPanelSettings,
                text = "超级用户",
                checked = item.isSuperUser,
                onCheckedChange = { isExpanded = false; viewModel.updateSuPolicy(item, enable = it) }
            )
            if (item.isSuperUserActive) {
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "撤销超级用户",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = { isExpanded = false; viewModel.updateSuPolicy(item) }
                )
            }
        }
    }
}
