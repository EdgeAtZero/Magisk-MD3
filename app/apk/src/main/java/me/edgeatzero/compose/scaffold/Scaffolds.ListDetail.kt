package me.edgeatzero.compose.scaffold

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.edgeatzero.compose.util.UIMode
import me.edgeatzero.compose.util.translucentTopAppBarColors
import me.edgeatzero.compose.util.transparencyBackground
import me.edgeatzero.compose.util.updateBarColor

fun Scaffolds.anchors(size: Dp = 400.dp): List<PaneExpansionAnchor> =
    listOf(
        PaneExpansionAnchor.Proportion(0f),
        PaneExpansionAnchor.Offset.fromStart(size),
        PaneExpansionAnchor.Offset.fromEnd(size),
        PaneExpansionAnchor.Proportion(1f)
    )

@ExperimentalMaterial3Api
@ExperimentalMaterial3AdaptiveApi
@Composable
fun <T> Scaffolds.ListDetail(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    navigator: ThreePaneScaffoldNavigator<T>,
    topBar: @Composable (TopAppBarColors, TopAppBarScrollBehavior) -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    paneAnchors: List<PaneExpansionAnchor> = anchors(),
    initialAnchoredIndex: Int = paneAnchors.lastIndex,
    paneExpansionState: PaneExpansionState = rememberPaneExpansionState(
        navigator.scaffoldValue,
        paneAnchors,
        initialAnchoredIndex
    ),
    listPane: @Composable ThreePaneScaffoldPaneScope.(PaddingValues) -> Unit,
    backControl: Boolean = true,
    detailPane: @Composable ThreePaneScaffoldPaneScope.(PaddingValues) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val topAppBarColors = TopAppBarDefaults.translucentTopAppBarColors()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior().updateBarColor(topAppBarColors)
    val contentKey by remember { derivedStateOf { navigator.currentDestination?.contentKey } }

    if (backControl) {
        BackHandler(UIMode.current == UIMode.TABLET && paneExpansionState.currentAnchor != paneAnchors.last()) {
            if (contentKey == null) {
                coroutineScope.launch {
                    paneExpansionState.animateTo(paneAnchors.last())
                }
            }
        }
        BackHandler(contentKey != null) {
            coroutineScope.launch {
                navigator.navigateBack()
            }
            if (paneExpansionState.currentAnchor == paneAnchors.first()) {
                coroutineScope.launch {
                    paneExpansionState.animateTo(paneAnchors[1])
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = { topBar(topAppBarColors.transparencyBackground(), topAppBarScrollBehavior) },
        floatingActionButton = floatingActionButton
    ) { contentPadding ->
        val combinedContentPadding = rootContentPadding + contentPadding
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            paneExpansionState = paneExpansionState,
            paneExpansionDragHandle = {
                val interactionSource = remember { MutableInteractionSource() }
                VerticalDragHandle(
                    modifier = Modifier.paneExpansionDraggable(
                        state = paneExpansionState,
                        minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                        interactionSource = interactionSource
                    ),
                    interactionSource = interactionSource
                )
            },
            listPane = { listPane(combinedContentPadding) },
            detailPane = { detailPane(combinedContentPadding) }
        )
    }
}
