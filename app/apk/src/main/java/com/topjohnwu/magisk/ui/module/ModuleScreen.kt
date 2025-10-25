package com.topjohnwu.magisk.ui.module

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.ui.web.WebActivity
import me.edgeatzero.compose.scaffold.Basic
import me.edgeatzero.compose.scaffold.Scaffolds
import me.edgeatzero.compose.scaffold.TopBars
import me.edgeatzero.compose.theme.Elevation
import me.edgeatzero.compose.util.plus
import me.edgeatzero.compose.util.top

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ModuleScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: ModuleViewModel,
    onModuleAction: (String, String) -> Unit,
    onModuleInstall: (String) -> Unit
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Basic(
            title = { Text(text = "模块") }
        )
    ) { contentPadding ->
        Crossfade(modifier = Modifier.fillMaxSize(), targetState = viewModel.isLoading) { targetState ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (targetState) {
                    CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    PullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        onRefresh = { viewModel.refresh() },
                        isRefreshing = viewModel.isRefreshing,
                        state = pullToRefreshState,
                        indicator = {
                            PullToRefreshDefaults.LoadingIndicator(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = contentPadding.top),
                                state = pullToRefreshState,
                                elevation = Elevation.Level3,
                                isRefreshing = viewModel.isRefreshing
                            )
                        }
                    ) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(300.dp),
                            contentPadding = contentPadding + 16.dp,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 16.dp
                        ) {
                            items(viewModel.modules, key = { it.id }) { item ->
                                ModuleInfoCard(
                                    modifier = Modifier.animateItem(),
                                    item = item,
                                    onAction = { onModuleAction(item.id, item.name) },
                                    onUpdate = { p0, p1 -> viewModel.updateModule(item, p0, p1) },
                                    onWeb = { WebActivity.launch(context, item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
