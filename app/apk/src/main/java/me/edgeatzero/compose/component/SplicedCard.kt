package me.edgeatzero.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.edgeatzero.compose.util.ProvideContentColorTextStyle

// copied from https://github.com/wxxsfxyzm/InstallerX-Revived/blob/main/app/src/main/java/com/rosan/installer/ui/page/main/widget/setting/SettingsGroup.kt

@Composable
fun SplicedCard(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    cornerShape: CornerBasedShape = MaterialTheme.shapes.medium,
    connectionShape: CornerBasedShape = MaterialTheme.shapes.extraSmall,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    content: List<@Composable () -> Unit>,
) {
    if (content.isEmpty()) return

    // Define shapes for different positions.
    val topShape = remember {
        RoundedCornerShape(
            topStart = cornerShape.topStart,
            topEnd = cornerShape.topEnd,
            bottomStart = connectionShape.bottomStart,
            bottomEnd = connectionShape.bottomEnd
        )
    }
    val bottomShape = remember {
        RoundedCornerShape(
            topStart = connectionShape.topStart,
            topEnd = connectionShape.topEnd,
            bottomStart = cornerShape.bottomStart,
            bottomEnd = cornerShape.bottomEnd
        )
    }

    Column(modifier = modifier) {
        // Group title
        title?.let {
            Box(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                ProvideContentColorTextStyle(
                    contentColor = MaterialTheme.colorScheme.primary,
                    textStyle = MaterialTheme.typography.titleSmall,
                    content = it
                )
            }
        }

        // The container for setting items.
        Column(
            modifier = Modifier
                // Clip the whole column to ensure content stays within the rounded bounds.
                .clip(cornerShape),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            content.forEachIndexed { index, item ->
                // Determine the shape based on the pkg's position.
                val shape = when {
                    content.size == 1 -> cornerShape
                    index == 0 -> topShape
                    index == content.size - 1 -> bottomShape
                    else -> connectionShape
                }

                // Apply the background and the correct shape to the pkg.
                Box(modifier = Modifier.clip(shape).background(color = containerColor)) {
                    item()
                }
            }
        }
    }
}
