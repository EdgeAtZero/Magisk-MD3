package com.topjohnwu.magisk.ui.superuser

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import me.edgeatzero.compose.component.SearchBar
import me.edgeatzero.compose.scaffold.*
import me.edgeatzero.compose.util.calculatePaneScaffoldDirective
import me.edgeatzero.compose.util.onBackPressed

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
    val navigator = rememberListDetailPaneScaffoldNavigator<String>(calculatePaneScaffoldDirective())
    val paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue, ANCHORS, ANCHORS.lastIndex)
    val contentKey by remember { derivedStateOf { navigator.currentDestination?.contentKey } }
    var isAdvancedMenuSheetVisible by rememberSaveable { mutableStateOf(false) }
    var isSearchBarShouldVisible by rememberSaveable { mutableStateOf(false) }

    if (isAdvancedMenuSheetVisible) {
        SuperUserAdvancedMenuBottomSheet(viewModel = viewModel) {
            isAdvancedMenuSheetVisible = false
        }
    }

    Scaffolds.ListDetail(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        navigator = navigator,
        paneAnchors = ANCHORS,
        paneExpansionState = paneExpansionState,
        topBar = TopBars.Switchable(
            isSwitched = contentKey == null && isSearchBarShouldVisible,
            title = { Text(text = if (contentKey != null) "应用信息" else "超级用户") },
            actions = {
                AnimatedVisibility(
                    visible = contentKey == null,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                ) {
                    Row {
                        IconButton(onClick = { isSearchBarShouldVisible = true }) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                        }
                        IconButton(
                            onClick = { isAdvancedMenuSheetVisible = true },
                            content = { Icon(imageVector = Icons.Filled.FilterList, contentDescription = null) }
                        )
                    }
                }
            },
            navigationIcon = {
                AnimatedVisibility(
                    visible = contentKey != null,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
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
        ),
        listPane = { contentPadding ->
            AnimatedPane {
                SuperUserList(
                    contentPadding = contentPadding,
                    viewModel = viewModel,
                    currentPkg = contentKey
                ) {
                    coroutineScope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, it) }
                    if (paneExpansionState.currentAnchor == ANCHORS.last()) {
                        coroutineScope.launch { paneExpansionState.animateTo(ANCHORS[1]) }
                    }
                }
            }
        },
        detailPane = { contentPadding ->
            AnimatedPane {
                Crossfade(targetState = viewModel.apps.find { contentKey == it.packageName }) { targetState ->
                    if (targetState != null) {
                        SuperUserDetail(
                            contentPadding = contentPadding,
                            item = targetState
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "选择一个程序以查看选项")
                        }
                    }
                }
            }
        }
    )
}
