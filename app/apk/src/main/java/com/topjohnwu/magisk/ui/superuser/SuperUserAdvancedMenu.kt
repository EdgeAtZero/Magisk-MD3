package com.topjohnwu.magisk.ui.superuser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
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

private val AppFilter.text: String
    get() = when (this) {
        AppFilter.ALL -> "全部应用"
        AppFilter.SYSTEM -> "系统应用"
        AppFilter.USER -> "用户应用"
    }

private val AppSort.text: String
    get() = when (this) {
        AppSort.Name -> "应用名称"
        AppSort.Package -> "应用包名"
        AppSort.UID -> "UID"
    }

private val AppPriority.text: String
    get() = when (this) {
        AppPriority.Deny -> "排除应用"
        AppPriority.SuperUser -> "超级应用"
    }

@ExperimentalMaterial3Api
@Composable
fun SuperUserAdvancedMenu(
    modifier: Modifier = Modifier,
    viewModel: SuperUserViewModel,
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
                        headlineContent = { Text(text = "筛选应用") },
                        overlineContent = {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppFilter.entries.forEach { item ->
                                    FilterChip(
                                        selected = viewModel.filter == item,
                                        label = { Text(text = item.text) },
                                        onClick = { viewModel.filter = item }
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.Filled.FilterList, contentDescription = null)
                        }
                    )
                },
                {

                    ListItem(
                        headlineContent = { Text(text = "排序") },
                        overlineContent = {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppSorts.forEach { item ->
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
                                AppPriorities.forEach { item ->
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
