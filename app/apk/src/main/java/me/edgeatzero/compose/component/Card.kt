package me.edgeatzero.compose.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape

@Composable
fun Card(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = shape,
        border = border,
        onClick = null,
        indication = null,
        interactionSource = null,
        content = content
    )
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    indication: Indication? = onClick?.let { LocalIndication.current },
    interactionSource: MutableInteractionSource? = onClick?.let { remember { MutableInteractionSource() } },
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .clip(shape)
            .then(modifier)
            .then(
                onClick?.let {
                    modifier.clickable(
                        enabled = enabled,
                        indication = indication,
                        interactionSource = interactionSource,
                        onClick = it
                    )
                } ?: Modifier
            ),
        color = if (enabled) colors.containerColor else colors.disabledContainerColor,
        contentColor = if (enabled) colors.contentColor else colors.disabledContentColor,
        shape = shape,
        border = border,
        content = content
    )
}
