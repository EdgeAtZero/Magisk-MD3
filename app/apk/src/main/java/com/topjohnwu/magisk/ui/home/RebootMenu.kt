package com.topjohnwu.magisk.ui.home

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.topjohnwu.magisk.core.Config
import com.topjohnwu.magisk.core.Const
import com.topjohnwu.magisk.core.ktx.reboot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RebootMenu(modifier: Modifier = Modifier) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isSafeMode by rememberSaveable { mutableStateOf(Config.bootloop >= 2) }

    LaunchedEffect(isSafeMode) {
        val target = if (isSafeMode) 2 else 0
        if (Config.bootloop != target) {
            Config.bootloop = target
        }
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        IconButton(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            onClick = {},
        ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
        }
        ExposedDropdownMenu(
            expanded = isExpanded,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            matchAnchorWidth = false,
            onDismissRequest = { isExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = "重启") },
                onClick = { reboot() }
            )
            DropdownMenuItem(
                text = { Text(text = "软重启") },
                onClick = { reboot("userspace") }
            )
            DropdownMenuItem(
                text = { Text(text = "重启到 Recovery") },
                onClick = { reboot("recovery") }
            )
            DropdownMenuItem(
                text = { Text(text = "重启到 Bootloader") },
                onClick = { reboot("bootloader") }
            )
            DropdownMenuItem(
                text = { Text(text = "重启到 Download") },
                onClick = { reboot("download") }
            )
            DropdownMenuItem(
                text = { Text(text = "重启到 EDL") },
                onClick = { reboot("edl") }
            )
            if (Const.Version.atLeast_28_0()) {
                val interactionSource = remember { MutableInteractionSource() }
                DropdownMenuItem(
                    text = { Text(text = "安全模式") },
                    trailingIcon = {
                        Switch(checked = isSafeMode, onCheckedChange = null, interactionSource = interactionSource)
                    },
                    interactionSource = interactionSource,
                    onClick = { isSafeMode = !isSafeMode }
                )
            }
        }
    }
}
