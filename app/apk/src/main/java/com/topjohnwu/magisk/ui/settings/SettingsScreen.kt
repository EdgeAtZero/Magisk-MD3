package com.topjohnwu.magisk.ui.settings

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.topjohnwu.magisk.ui.util.dynamicBarBackgroundColor
import com.topjohnwu.magisk.ui.util.updateBarBackgroundColor
import me.edgeatzero.compose.util.translucentTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
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
                title = { Text("设置") },
                scrollBehavior = scrollBehavior,
                colors = colors.copy(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { contentPadding ->
        val data = viewModel<SettingsViewModel>().data
        LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = contentPadding) {
            items(data.filter { !it.isNotEnabled }, key = { it.id }) { item ->
                Item(Modifier.animateItem(), item)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Item(modifier: Modifier = Modifier, item: SampleData) {
    ListItem(modifier = modifier) {
        Text(item.name)
    }
}
