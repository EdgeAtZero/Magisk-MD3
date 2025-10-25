package com.topjohnwu.magisk.ui.install

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun InstallMethodCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    method: InstallMethod?,
    onMethodChanged: (InstallMethod) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "安装方式")
        }
        InstallMethods.filter { it.isAvailable }.forEach { item ->
            val isSelected = method == item

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = isSelected,
                        onValueChange = { onMethodChanged(item) },
                        role = Role.Checkbox,
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null
                )
                Text(
                    text = when (item) {
                        InstallMethod.Patch -> "选择并修补文件"
                        InstallMethod.Direct -> "直接安装（推荐）"
                        InstallMethod.InactiveSlot -> "安装到未使用的槽位（OTA后）"
                    }
                )
            }
        }
    }
}
