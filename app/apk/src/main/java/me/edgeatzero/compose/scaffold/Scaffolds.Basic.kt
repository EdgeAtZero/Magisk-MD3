package me.edgeatzero.compose.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import me.edgeatzero.compose.util.translucentTopAppBarColors
import me.edgeatzero.compose.util.transparencyBackground
import me.edgeatzero.compose.util.updateBarColor

@ExperimentalMaterial3Api
@Composable
fun Scaffolds.Basic(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    topBar: @Composable (TopAppBarColors, TopAppBarScrollBehavior) -> Unit = { _, _ -> },
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val topAppBarColors = TopAppBarDefaults.translucentTopAppBarColors()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior().updateBarColor(topAppBarColors)

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = { topBar(topAppBarColors.transparencyBackground(), topAppBarScrollBehavior) },
        floatingActionButton = floatingActionButton,
        content = { contentPadding -> content(rootContentPadding + contentPadding) }
    )
}
