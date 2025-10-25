package me.edgeatzero.compose.util

import androidx.compose.material3.TopAppBarColors
import androidx.compose.ui.graphics.Color

fun TopAppBarColors.transparencyBackground() =
    copy(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent)
