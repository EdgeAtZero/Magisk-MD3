package me.edgeatzero.compose.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TopAppBarDefaults.translucentTopAppBarColors(
    alpha: Float = 0.9f,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    scrolledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = alpha),
    navigationIconContentColor: Color = Color.Unspecified,
    titleContentColor: Color = Color.Unspecified,
    actionIconContentColor: Color = Color.Unspecified,
): TopAppBarColors = topAppBarColors(
    containerColor,
    scrolledContainerColor,
    navigationIconContentColor,
    titleContentColor,
    actionIconContentColor
)
