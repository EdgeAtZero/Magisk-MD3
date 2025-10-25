package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.ui.superuser.AppSort.Order
import sh.calvin.reorderable.ReorderableColumn

private val AppSort<*>.text: String
    get() = when (this) {
        AppSort.AppName -> "应用名称"
        AppSort.PackageName -> "应用包名"
        AppSort.UID -> "UID"
        AppSort.WhetherDeny -> "排除应用"
        AppSort.WhetherSU -> "SU应用"
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppSortMenu(
    modifier: Modifier = Modifier,
    list: List<AppSortData>,
    onListChanged: (List<AppSortData>) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        SplitButtonLayout(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            leadingButton = {
                SplitButtonDefaults.TonalLeadingButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "排序")
                }
            },
            trailingButton = {
                SplitButtonDefaults.TonalTrailingButton(
                    checked = isExpanded,
                    onCheckedChange = {}
                ) {
                    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f)
                    Icon(
                        modifier = Modifier
                            .size(SplitButtonDefaults.TrailingIconSize)
                            .graphicsLayer { this.rotationZ = rotation },
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
            }
        )
        ExposedDropdownMenu(
            modifier = Modifier.height(200.dp),
            expanded = isExpanded,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            matchAnchorWidth = false,
            onDismissRequest = { isExpanded = false },
        ) {
            ReorderableColumn(
                list = list,
                onSettle = { from, to -> onListChanged(list.toMutableList().apply { add(to, removeAt(from)) }) },
            ) { index, (sort, order), isDragging ->
                key(sort) {
                    ReorderableItem {
                        DropdownMenuItem(
                            leadingIcon = {
                                val rotation by animateFloatAsState(if (order == Order.ASC) 0f else 180f)
                                Icon(
                                    modifier = Modifier.graphicsLayer { this.rotationZ = rotation },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                            },
                            text = { Text(text = sort.text) },
                            trailingIcon = {
                                val scale by animateFloatAsState(if (isDragging) 1.25f else 1f)
                                Icon(
                                    modifier = Modifier
                                        .draggableHandle()
                                        .graphicsLayer { this.scaleX = scale; this.scaleY = scale },
                                    imageVector = Icons.Filled.DragHandle,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                if (sort.isNeedOrder) {
                                    onListChanged(list.toMutableList().apply { this[index] = sort to !order })
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
