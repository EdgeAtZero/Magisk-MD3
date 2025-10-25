package com.topjohnwu.magisk.ui.install

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun InstallOptionsCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    options: SnapshotStateList<InstallOption>
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "选项")
        }
        InstallOptions.filter { it.isAvailable }.forEach { item ->
            val isSelected = options.contains(item)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = isSelected,
                        onValueChange = { options += item },
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
                        InstallOption.KeepVerity -> "保留 AVB 2.0/dm-verity"
                        InstallOption.KeepEncryption -> "保持强制加密"
                        InstallOption.Recovery -> "安装到 Recovery"
                    }
                )
            }
        }
    }
}
