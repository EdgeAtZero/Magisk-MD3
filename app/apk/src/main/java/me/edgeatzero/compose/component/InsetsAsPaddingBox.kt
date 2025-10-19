package me.edgeatzero.compose.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.edgeatzero.compose.util.asPaddingValues

@Composable
fun InsetsAsPaddingBox(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.(drawPadding: PaddingValues) -> Unit
) {
    val insets = WindowInsets.statusBars
    Box(modifier = modifier.consumeWindowInsets(insets), contentAlignment = contentAlignment) {
        content(insets.asPaddingValues())
    }
}
