package me.edgeatzero.compose.component

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ListSelectableItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean,
    indication: Indication = LocalIndication.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onSelectedChanged: (Boolean) -> Unit = {},
    colors: ListItemColors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit) = {
        Radio(
            enabled = enabled,
            selected = selected,
            interactionSource = interactionSource,
            onSelectedChanged = onSelectedChanged
        )
    }
) {
    ListItem(
        modifier = modifier,
        enabled = enabled,
        indication = indication,
        interactionSource = interactionSource,
        onClick = { onSelectedChanged(!selected) },
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors
    )
}
