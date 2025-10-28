package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.edgeatzero.compose.theme.Elevation
import me.edgeatzero.compose.util.top

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SuperUserList(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentPadding: PaddingValues,
    currentPkg: String? = null,
    viewModel: SuperUserViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    Crossfade(modifier = modifier, targetState = viewModel.isLoading) { targetState ->
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
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        contentPadding = contentPadding
                    ) {
                        items(items = viewModel.apps, key = { item -> item.packageName }) { item ->
                            AppInfoCard(
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .animateItem(),
                                viewModel = viewModel,
                                item = item,
                                subtext = { if (viewModel.sort == AppSort.UID) "UID: ${it.uid}" else it.packageName },
                                isQuickSettingsEnable = true,
                                isShowLabel = true,
                                isSelected = currentPkg == item.packageName
                            ) {
                                onNavigateToDetail(item.packageName)
                            }
                        }
                    }
                }
            }
        }
    }
}
