package com.topjohnwu.magisk.ui.install

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.edgeatzero.compose.component.ListSelectableItem
import me.edgeatzero.compose.component.SplicedCard

@Composable
fun InstallMethodCard(
    modifier: Modifier = Modifier,
    method: InstallMethod?,
    onMethodChanged: (InstallMethod) -> Unit
) {
    SplicedCard(
        modifier = modifier,
        title = {
            Text(text = "安装方式")
        },
        content = InstallMethods.filter { it.isAvailable }.map { item ->
            {
                ListSelectableItem(
                    selected = method == item,
                    headlineContent = {
                        Text(
                            text = when (item) {
                                InstallMethod.Patch -> "选择并修补文件"
                                InstallMethod.Direct -> "直接安装（推荐）"
                                InstallMethod.InactiveSlot -> "安装到未使用的槽位（OTA后）"
                            }
                        )
                    },
                    onSelectedChanged = { onMethodChanged(item) }
                )
            }
        }
    )
}
