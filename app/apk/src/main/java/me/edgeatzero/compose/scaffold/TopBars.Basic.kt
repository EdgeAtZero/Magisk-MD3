package me.edgeatzero.compose.scaffold

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.edgeatzero.compose.util.dynamicBarColor

@ExperimentalMaterial3Api
@ExperimentalMaterial3ExpressiveApi
@Composable
fun TopBars.Basic(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) = @Composable { p0: TopAppBarColors, p1: TopAppBarScrollBehavior ->
    LargeTopAppBar(
        modifier = Modifier
            .dynamicBarColor()
            .statusBarsPadding(),
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = p0,
        scrollBehavior = p1,
    )
}
