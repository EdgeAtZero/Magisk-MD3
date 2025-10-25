package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.topjohnwu.magisk.ui.component.TextLabel
import me.edgeatzero.compose.theme.MaterialColors


@ExperimentalMaterial3Api
@Composable
fun AppInfoCard(
    modifier: Modifier = Modifier,
    item: AppInfo,
    viewModel: SuperUserViewModel? = null,
    isQuickSettingsEnable: Boolean = true,
    isSelected: Boolean = false,
    isShowLabel: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .let {
                if (onClick != null) it.clickable(onClick = onClick, role = Role.Button) else it
            }
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp) else Color.Unspecified
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                modifier = Modifier.size(38.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.packageInfo)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                if (item.label == item.packageName) {
                    Text(
                        text = item.label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = item.label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = item.packageName,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                if (isShowLabel) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .animateContentSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (item.isSystemApp) {
                            TextLabel(
                                text = "SYSTEM",
                                containerColor = MaterialColors.Amber[200],
                                contentColor = MaterialColors.Amber[900]
                            )
                        }
                        if (item.isSharedUID) {
                            TextLabel(
                                text = "SharedUID",
                                containerColor = MaterialColors.Purple[100],
                                contentColor = MaterialColors.Purple[900]
                            )
                        }
                        AnimatedVisibility(visible = item.isSuperUserActive) {
                            AnimatedContent(targetState = item.isSuperUser) {
                                if (it) {
                                    TextLabel(
                                        text = "SU",
                                        containerColor = MaterialColors.Red[600],
                                        contentColor = MaterialColors.Red[50]
                                    )
                                } else {
                                    TextLabel(
                                        text = "SU REJECT",
                                        containerColor = MaterialColors.Green[500],
                                        contentColor = MaterialColors.Green[50]
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(visible = item.isDeny) {
                            TextLabel(
                                text = "DENY",
                                containerColor = MaterialColors.LightBlue[100],
                                contentColor = MaterialColors.LightBlue[900]
                            )
                        }
                    }
                }
            }
            if (isQuickSettingsEnable && viewModel != null) {
                AppInfoMenu(item = item, viewModel = viewModel)
            }
        }
    }
}
