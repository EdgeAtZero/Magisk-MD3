package me.edgeatzero.compose.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton as Material3Radio
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Radio(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean,
    onSelectedChanged: ((Boolean) -> Unit)?,
    interactionSource: MutableInteractionSource? = onSelectedChanged?.let { remember { MutableInteractionSource() } },
    colors: RadioButtonColors = RadioButtonDefaults.colors()
) {
    Material3Radio(
        selected = selected,
        onClick = onSelectedChanged?.let { { it(!selected) } },
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
            .then(modifier),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}
