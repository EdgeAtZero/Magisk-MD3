package me.edgeatzero.compose.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

private val LocalBarBackgroundColor = compositionLocalOf {
    mutableStateOf(Color.Unspecified)
}

val ColorScheme.dynamicBarColor: Color
    @Composable
    get() {
        val targetColor = LocalBarBackgroundColor.current.value
        return animateColorAsState(targetColor).value
    }

@Composable
fun Modifier.dynamicBarColor(): Modifier {
    val targetColor = LocalBarBackgroundColor.current.value
    val appBarContainerColor by animateColorAsState(targetColor)
    return drawBehind {
        val color = appBarContainerColor
        if (color != Color.Unspecified) {
            drawRect(color = color)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun TopAppBarScrollBehavior.updateBarColor(colors: TopAppBarColors): TopAppBarScrollBehavior {
    val barBackgroundColor = LocalBarBackgroundColor.current
    val key1 = state.collapsedFraction
    DisposableEffect(key1) {
        barBackgroundColor.value = lerp(
            colors.containerColor,
            colors.scrolledContainerColor,
            FastOutLinearInEasing.transform(key1),
        )
        onDispose {}
    }
    return this
}
