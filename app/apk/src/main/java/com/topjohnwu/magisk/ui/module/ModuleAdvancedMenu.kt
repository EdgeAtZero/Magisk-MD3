package com.topjohnwu.magisk.ui.module

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import me.edgeatzero.android.setupWindowBlurListener
import me.edgeatzero.compose.component.ListItem
import me.edgeatzero.compose.component.SplicedCard

private val ModuleSort.text: String
    get() = when (this) {
        ModuleSort.ID -> "ID"
        ModuleSort.Name -> "模块名称"
        ModuleSort.Size -> "模块大小"
    }

private val ModulePriority.text: String
    get() = when (this) {
        ModulePriority.Acton -> "可执行"
        ModulePriority.Update -> "可更新"
        ModulePriority.Web -> "Web"
    }

@ExperimentalMaterial3Api
@Composable
fun ModuleAdvancedMenu(
    modifier: Modifier = Modifier,
    viewModel: ModuleViewModel,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismissRequest
    ) {
        (LocalView.current.parent as DialogWindowProvider).setupWindowBlurListener()
        SplicedCard(
            modifier = Modifier.padding(24.dp),
            title = { Text(text = "高级菜单") },
            content = listOf(
                {
                    ListItem(
                        headlineContent = { Text(text = "排序") },
                        overlineContent = {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ModuleSorts.forEach { item ->
                                    FilterChip(
                                        selected = viewModel.sort == item,
                                        label = { Text(text = item.text) },
                                        onClick = { viewModel.sort = item }
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = null)
                        }
                    )
                },
                {
                    ListItem(
                        headlineContent = { Text(text = "优先") },
                        overlineContent = {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ModulePriorities.forEach { item ->
                                    val isSelected = viewModel.priorities.contains(item)

                                    FilterChip(
                                        selected = isSelected,
                                        label = { Text(text = item.text) },
                                        onClick = {
                                            viewModel.priorities = if (isSelected) {
                                                viewModel.priorities - item
                                            } else {
                                                viewModel.priorities + item
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.Filled.Upgrade, contentDescription = null)
                        }
                    )
                }
            )
        )
    }
}
