package com.topjohnwu.magisk.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.Info
import me.edgeatzero.compose.component.Column
import me.edgeatzero.compose.scaffold.Basic
import me.edgeatzero.compose.scaffold.Scaffolds
import me.edgeatzero.compose.scaffold.TopBars
import me.edgeatzero.compose.util.plus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: HomeViewModel,
    onNavigateToInstall: () -> Unit,
    onNavigateToUninstall: () -> Unit
) {
    if (viewModel.envCheckCode != 0) {
        var isConfirmed by rememberSaveable { mutableStateOf(false) }
        if (!isConfirmed) {
            EnvFixDialog(
                code = viewModel.envCheckCode,
                onDismissRequest = { isConfirmed = true },
                onNavigateToInstaller = onNavigateToInstall
            )
        }
    }

    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Basic(
            title = { Text(text = "Magisk") },
            actions = {
                if (Info.env.isActive) {
                    RebootMenu()
                    IconButton(onClick = onNavigateToInstall) {
                        Icon(imageVector = Icons.Outlined.SystemUpdate, contentDescription = null)
                    }
                }
            }
        )
    ) { contentPadding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(400.dp),
            contentPadding = contentPadding + 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            item {
                StatusCard(modifier = Modifier.animateItem(), onNavigateToInstall = onNavigateToInstall)
            }
            item {
                InfoCard(modifier = Modifier.animateItem())
            }
        }
    }
}
