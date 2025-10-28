package com.topjohnwu.magisk.ui.module

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import me.edgeatzero.android.setupWindowBlurListener

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
fun ModuleAdvancedMenuBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: ModuleViewModel,
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
                Text(text = "排序", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow {
                    ModuleSorts.forEachIndexed { index, entry ->
                        SegmentedButton(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            selected = viewModel.sort == entry,
                            shape = SegmentedButtonDefaults.itemShape(index, ModuleSorts.size),
                            onClick = { viewModel.sort = entry }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = entry.text)
                            }
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "优先", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow {
                    ModulePriorities.forEachIndexed { index, entry ->
                        val isSelected = viewModel.priorities.contains(entry)

                        SegmentedButton(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            selected = isSelected,
                            shape = SegmentedButtonDefaults.itemShape(index, ModulePriorities.size),
                            onClick = {
                                viewModel.priorities = if (isSelected) {
                                    viewModel.priorities - entry
                                } else {
                                    viewModel.priorities + entry
                                }
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = entry.text)
                            }
                        }
                    }
                }
            }
        }
    }
}
