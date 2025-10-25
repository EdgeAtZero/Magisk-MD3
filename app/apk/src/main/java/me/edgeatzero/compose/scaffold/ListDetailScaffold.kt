package me.edgeatzero.compose.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import me.edgeatzero.compose.util.calculatePaneScaffoldDirective
import me.edgeatzero.compose.util.translucentTopAppBarColors
import me.edgeatzero.compose.util.updateBarColor

private val PANE_ANCHORS = listOf(
    PaneExpansionAnchor.Proportion(0f),
    PaneExpansionAnchor.Offset.fromStart(400.dp),
    PaneExpansionAnchor.Offset.fromEnd(400.dp),
    PaneExpansionAnchor.Proportion(1f)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailScaffold(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    paneAnchors: List<PaneExpansionAnchor> = PANE_ANCHORS,
    list: @Composable ThreePaneScaffoldPaneScope.() -> Unit,
    detail: @Composable ThreePaneScaffoldPaneScope.() -> Unit
) {
    val topAppBarColors = TopAppBarDefaults.translucentTopAppBarColors()
    TopAppBarDefaults.exitUntilCollapsedScrollBehavior().updateBarColor(topAppBarColors)
    val paneDirective = calculatePaneScaffoldDirective()
    val paneNavigator = rememberListDetailPaneScaffoldNavigator<String>(paneDirective)
    val paneExpansionState = rememberPaneExpansionState(paneNavigator.scaffoldValue, PANE_ANCHORS, 3)
}
