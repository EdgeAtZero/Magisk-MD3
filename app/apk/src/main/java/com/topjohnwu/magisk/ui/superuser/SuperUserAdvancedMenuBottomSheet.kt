package com.topjohnwu.magisk.ui.superuser

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import me.edgeatzero.android.setupWindowBlurListener

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
fun SuperUserAdvancedMenuBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: SuperUserViewModel,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismissRequest
    ) {
        (LocalView.current.parent as DialogWindowProvider).setupWindowBlurListener()
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "高级菜单",
            style = MaterialTheme.typography.headlineSmall
        )
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "筛选应用", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow {
                    AppFilter.entries.forEachIndexed { index, entry ->
                        SegmentedButton(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            selected = viewModel.filter == entry,
                            onClick = { viewModel.filter = entry },
                            shape = SegmentedButtonDefaults.itemShape(index, AppFilter.entries.size)
                        ) {
                            Text(text = entry.text)
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "排序", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow {
                    AppSorts.forEachIndexed { index, entry ->
                        SegmentedButton(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            selected = viewModel.sort == entry,
                            shape = SegmentedButtonDefaults.itemShape(index, AppSorts.size),
                            onClick = { viewModel.sort = entry }
                        ) {
                                Text(text = entry.text)
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "优先", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow {
                    AppPriorities.forEachIndexed { index, entry ->
                        val isSelected = viewModel.priorities.contains(entry)

                        SegmentedButton(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            selected = isSelected,
                            shape = SegmentedButtonDefaults.itemShape(index, AppPriorities.size),
                            onClick = {
                                viewModel.priorities = if (isSelected) {
                                    viewModel.priorities - entry
                                } else {
                                    viewModel.priorities + entry
                                }
                            }
                        ) {
                                Text(text = entry.text)
                        }
                    }
                }
            }
        }
    }
}
