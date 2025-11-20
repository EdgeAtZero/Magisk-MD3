package me.edgeatzero.compose.component

import androidx.compose.animation.*
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun ListExpandableItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expanded: Boolean = true,
    colors: ListItemColors = ListItemDefaults.colors(),
    onClick: (() -> Unit)? = null,
    indication: Indication? = onClick?.let { LocalIndication.current },
    interactionSource: MutableInteractionSource? = onClick?.let { remember { MutableInteractionSource() } },
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        onClick = onClick,
        indication = indication,
        interactionSource = interactionSource,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        overlineContent = overlineContent?.let {
            {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                ) {
                    it()
                }
            }
        },
        leadingContent = leadingContent,
        trailingContent = trailingContent
    )
}
