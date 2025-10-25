package me.edgeatzero.compose.util

import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val LocalUIMode = staticCompositionLocalOf<UIMode> {
    error("No UIMode was provided via LocalUIMode")
}

enum class UIMode {
    MOBILE,
    TABLET;

    companion object {

        val current: UIMode @Composable get() = LocalUIMode.current

        val DEFAULT_TABLET_WIDTH = 600.dp

    }

}

@Composable
fun ProvideUIMode(mode: UIMode, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalUIMode provides mode, content = content)
}

@Composable
fun calculatePaneScaffoldDirective(): PaneScaffoldDirective =
    calculatePaneScaffoldDirective(LocalUIMode.current)

fun calculatePaneScaffoldDirective(mode: UIMode): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val horizontalPartitionSpacerSize: Dp
    val defaultPanePreferredWidth: Dp = UIMode.DEFAULT_TABLET_WIDTH / 2
    val maxVerticalPartitions: Int
    val verticalPartitionSpacerSize: Dp
    when (mode) {
        UIMode.MOBILE -> {
            maxHorizontalPartitions = 1
            horizontalPartitionSpacerSize = 0.dp
            maxVerticalPartitions = 1
            verticalPartitionSpacerSize = 0.dp
        }

        UIMode.TABLET -> {
            maxHorizontalPartitions = 2
            horizontalPartitionSpacerSize = 24.dp
            maxVerticalPartitions = 2
            verticalPartitionSpacerSize = 24.dp
        }
    }
    return PaneScaffoldDirective.Default.copy(
        maxHorizontalPartitions = maxHorizontalPartitions,
        horizontalPartitionSpacerSize = horizontalPartitionSpacerSize,
        maxVerticalPartitions = maxVerticalPartitions,
        verticalPartitionSpacerSize = verticalPartitionSpacerSize,
        defaultPanePreferredWidth = defaultPanePreferredWidth
    )
}
