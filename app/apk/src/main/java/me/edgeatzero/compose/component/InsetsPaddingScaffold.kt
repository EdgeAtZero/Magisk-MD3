package me.edgeatzero.compose.component

import androidx.compose.animation.core.animateRectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.edgeatzero.compose.util.*

@Composable
fun InsetsPaddingScaffold(
    modifier: Modifier = Modifier.fillMaxSize(),
    drawPadding: PaddingValues,
    start: (@Composable (startPadding: Dp) -> Unit)? = null,
    top: (@Composable (topPadding: Dp) -> Unit)? = null,
    end: (@Composable (endPadding: Dp) -> Unit)? = null,
    bottom: (@Composable (bottomPadding: Dp) -> Unit)? = null,
    content: @Composable (innerDrawPadding: PaddingValues) -> Unit
) {
    var startPadding by mutableStateOf(0f)
    var topPadding by mutableStateOf(0f)
    var endPadding by mutableStateOf(0f)
    var bottomPadding by mutableStateOf(0f)
    val animatePadding by animateRectAsState(Rect(startPadding, topPadding, endPadding, bottomPadding))
    val density = LocalDensity.current
    Surface(modifier = modifier) {
        Box {
            content.invoke(
                PaddingValues(
                    if (start != null) animatePadding.left.dp else drawPadding.start,
                    if (top != null) animatePadding.top.dp else drawPadding.top,
                    if (end != null) animatePadding.right.dp else drawPadding.end,
                    if (bottom != null) animatePadding.bottom.dp else drawPadding.bottom
                )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .onGloballyPositioned {
                        startPadding = with(density) { it.size.width.toDp().value }
                    }
            ) { start?.invoke(drawPadding.start) }
            Box(
                modifier = Modifier
                    .padding(start = startPadding.dp, end = endPadding.dp)
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned {
                        topPadding = with(density) { it.size.height.toDp().value }
                    }
            ) { top?.invoke(drawPadding.top) }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = startPadding.dp, end = endPadding.dp)
                    .onGloballyPositioned {
                        bottomPadding = with(density) { it.size.height.toDp().value }
                    }
            ) { bottom?.invoke(drawPadding.bottom) }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .onGloballyPositioned {
                        endPadding = with(density) { it.size.width.toDp().value }
                    }
            ) { end?.invoke(drawPadding.end) }
        }
    }
}
