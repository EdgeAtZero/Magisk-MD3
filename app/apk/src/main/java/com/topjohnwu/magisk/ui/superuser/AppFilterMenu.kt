package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

private val AppFilter.text: String
    get() = when (this) {
        AppFilter.ALL -> "全部"
        AppFilter.SYSTEM -> "系统"
        AppFilter.USER -> "用户"
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppFilterMenu(
    modifier: Modifier = Modifier,
    filter: AppFilter,
    onFilterSelected: (AppFilter) -> Unit
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
                        imageVector = Icons.Filled.FilterList,
                        modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = filter.text)
                }
            },
            trailingButton = {
                SplitButtonDefaults.TonalTrailingButton(checked = isExpanded, onCheckedChange = {}) {
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
            expanded = isExpanded,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            matchAnchorWidth = false,
            onDismissRequest = { isExpanded = false },
        ) {
            AppFilter.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.text) },
                    onClick = {
                        onFilterSelected(option)
                        isExpanded = false
                    }
                )
            }
        }
    }
}
