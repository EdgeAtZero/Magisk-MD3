package com.topjohnwu.magisk.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.edgeatzero.compose.scaffold.Basic
import me.edgeatzero.compose.scaffold.Scaffolds
import me.edgeatzero.compose.scaffold.TopBars

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    rootContentPadding: PaddingValues,
    viewModel: SettingsViewModel
) {
    Scaffolds.Basic(
        modifier = modifier,
        rootContentPadding = rootContentPadding,
        topBar = TopBars.Basic(
            title = { Text(text = "设置") }
        )
    ) { contentPadding ->
        val data = viewModel<SettingsViewModel>().data
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(3),
            contentPadding = contentPadding
        ) {
            items(data) { item ->
                Item(Modifier.animateItem(), item)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Item(modifier: Modifier = Modifier, item: String) {
    ListItem(modifier = modifier) {
        Text(item)
    }
}
