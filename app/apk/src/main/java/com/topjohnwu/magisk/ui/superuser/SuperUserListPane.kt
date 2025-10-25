package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.edgeatzero.compose.theme.Elevation
import me.edgeatzero.compose.util.copy
import me.edgeatzero.compose.util.top

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SuperUserListPane(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    currentPkg: String? = null,
    viewModel: SuperUserViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val pullToRefreshState = rememberPullToRefreshState()
    val isFabVisible by remember { derivedStateOf { lazyGridState.firstVisibleItemIndex != 0 || lazyGridState.firstVisibleItemScrollOffset != 0 } }

    Crossfade(modifier = modifier.fillMaxSize(), targetState = viewModel.isLoading) { targetState ->
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
                        state = lazyGridState,
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        contentPadding = contentPadding.copy(top = 0.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center
                    ) {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = contentPadding.top)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    AppFilterMenu(
                                        filter = viewModel.filter,
                                        onFilterSelected = { viewModel.filter = it }
                                    )
                                    AppSortMenu(
                                        list = viewModel.sort,
                                        onListChanged = { viewModel.sort = it }
                                    )
                                }
                            }
                        }
                        itemsIndexed(items = viewModel.apps, key = { index, item -> item.packageName }) { index, item ->
                            AppInfoCard(
                                modifier = Modifier.animateItem(),
                                viewModel = viewModel,
                                item = item,
                                isQuickSettingsEnable = true,
                                isShowLabel = true,
                                isSelected = currentPkg == item.packageName
                            ) {
                                onNavigateToDetail(item.packageName)
                            }
                        }
                    }
                    AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(contentPadding),
                        visible = isFabVisible,
                        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                    ) {
                        FloatingActionButton(
                            modifier = Modifier.padding(16.dp),
                            onClick = { coroutineScope.launch { lazyGridState.animateScrollToItem(0) } },
                            content = { Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}
