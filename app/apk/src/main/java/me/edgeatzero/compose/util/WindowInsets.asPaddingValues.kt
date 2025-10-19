package me.edgeatzero.compose.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun WindowInsets.asPaddingValues(): PaddingValues =
    asPaddingValues(LocalDensity.current, LocalLayoutDirection.current)

fun WindowInsets.asPaddingValues(
    destiny: Density,
    layoutDirection: LayoutDirection
): PaddingValues = with(destiny) {
    PaddingValues(
        getLeft(destiny, layoutDirection).toDp(),
        getTop(destiny).toDp(),
        getRight(destiny, layoutDirection).toDp(),
        getBottom(destiny).toDp()
    )
}
