package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.ui.navigation.MainDestination
import me.edgeatzero.compose.component.SearchBar
import me.edgeatzero.compose.scaffold.*
import me.edgeatzero.compose.theme.Elevation
import me.edgeatzero.compose.util.onBackPressed
import me.edgeatzero.compose.util.plus
import me.edgeatzero.compose.util.top

private val ANCHORS = Scaffolds.anchors()

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun SuperUserScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: SuperUserViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    var isAdvancedMenuVisible by rememberSaveable { mutableStateOf(false) }
    var isSearchBarShouldVisible by rememberSaveable { mutableStateOf(false) }

    if (isAdvancedMenuVisible) {
        SuperUserAdvancedMenu(viewModel = viewModel) {
            isAdvancedMenuVisible = false
        }
    }

    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Switchable(
            isSwitched = isSearchBarShouldVisible,
            title = { Text(text = MainDestination.SuperUser.label) },
            actions = {
                Row {
                    IconButton(onClick = { isSearchBarShouldVisible = true }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                    }
                    IconButton(
                        onClick = { isAdvancedMenuVisible = true },
                        content = { Icon(imageVector = Icons.Filled.FilterList, contentDescription = null) }
                    )
                }
            },
            switchedBar = {
                SearchBar(
                    value = viewModel.searchText,
                    onValueChange = { viewModel.searchText = it },
                    onRequestBack = { isSearchBarShouldVisible = false },
                    containerColor = Color.Transparent
                )
            }
        )
    ) { contentPadding ->
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
                        LazyVerticalStaggeredGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
                            contentPadding = contentPadding + 16.dp,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 16.dp
                        ) {
                            items(items = viewModel.apps, key = { item -> item.packageName }) { item ->
                                AppInfoCard(
                                    modifier = Modifier.width(IntrinsicSize.Max).animateItem(),
                                    viewModel = viewModel,
                                    item = item,
                                    subtext = { if (viewModel.sort == AppSort.UID) "UID: ${it.uid}" else it.packageName }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
