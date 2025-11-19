package com.topjohnwu.magisk.ui.install

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import me.edgeatzero.compose.component.ListSwitchItem
import me.edgeatzero.compose.component.SplicedCard

@Composable
fun InstallOptionsCard(
    modifier: Modifier = Modifier,
    options: SnapshotStateList<InstallOption>
) {
    SplicedCard(
        modifier = modifier,
        title = {
            Text(text = "选项")
        },
        content = InstallOptions.filter { it.isAvailable }.map { item ->
            {
                ListSwitchItem(
                    checked = options.contains(item),
                    headlineContent = {
                        Text(
                            text = when (item) {
                                InstallOption.KeepVerity -> "保留 AVB 2.0/dm-verity"
                                InstallOption.KeepEncryption -> "保持强制加密"
                                InstallOption.Recovery -> "安装到 Recovery"
                            }
                        )
                    },
                    onCheckedChange = {
                        if (it) {
                            options += item
                        } else {
                            options -= item
                        }
                    }
                )
            }
        }
    )
}
