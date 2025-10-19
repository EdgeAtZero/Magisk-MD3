package com.topjohnwu.magisk.ui.module

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.topjohnwu.magisk.ui.util.dynamicBarBackgroundColor
import com.topjohnwu.magisk.ui.util.updateBarBackgroundColor
import me.edgeatzero.compose.util.translucentTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreen(modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val colors = TopAppBarDefaults.translucentTopAppBarColors()
    scrollBehavior.updateBarBackgroundColor(colors)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                modifier = Modifier
                    .dynamicBarBackgroundColor()
                    .statusBarsPadding(),
                title = { Text("模块") },
                scrollBehavior = scrollBehavior,
                colors = colors.copy(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { contentPadding ->
    }
}
