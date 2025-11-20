package me.edgeatzero.compose.component

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.edgeatzero.compose.util.ProvideContentColorTextStyle


@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        modifier = modifier,
        colors = colors,
        onClick = null,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        overlineContent = overlineContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent
    )
}

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
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
    val decoratedHeadlineContent = @Composable {
        ProvideContentColorTextStyle(
            if (enabled) colors.headlineColor else colors.disabledHeadlineColor,
            MaterialTheme.typography.titleMedium,
            headlineContent,
        )
    }
    val decoratedSupportingContent = supportingContent?.let {
        @Composable {
            ProvideContentColorTextStyle(
                colors.supportingTextColor,
                MaterialTheme.typography.bodySmall,
                it,
            )
        }
    }
    val decoratedOverlineContent = overlineContent?.let {
        @Composable {
            ProvideContentColorTextStyle(
                colors.overlineColor,
                MaterialTheme.typography.labelSmall,
                it,
            )
        }
    }
    val decoratedLeadingContent = leadingContent?.let {
        @Composable {
            ProvideContentColorTextStyle(
                if (enabled) colors.leadingIconColor else colors.disabledLeadingIconColor,
                MaterialTheme.typography.titleMedium,
                content = it
            )
        }
    }
    val decoratedTrailingContent = trailingContent?.let {
        @Composable {
            ProvideContentColorTextStyle(
                if (enabled) colors.trailingIconColor else colors.disabledTrailingIconColor,
                MaterialTheme.typography.labelSmall,
                content = it,
            )
        }
    }

    Column(
        modifier = modifier
            .then(
                onClick?.let {
                    modifier.clickable(
                        enabled = true,
                        indication = indication,
                        interactionSource = interactionSource,
                        onClick = it
                    )
                } ?: Modifier
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.defaultMinSize(minHeight = 56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            decoratedLeadingContent?.let {
                Box(modifier = Modifier.padding(end = 16.dp)) {
                    it()
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                decoratedHeadlineContent()
                decoratedSupportingContent?.invoke()
            }
            decoratedTrailingContent?.let {
                Box(modifier = Modifier.padding(start = 16.dp)) {
                    it()
                }
            }
        }
        decoratedOverlineContent?.let {
            it()
        }
    }
}
