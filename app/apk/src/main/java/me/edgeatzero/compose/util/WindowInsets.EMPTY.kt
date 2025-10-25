package me.edgeatzero.compose.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Stable

private val INSTANCE = WindowInsets()

val WindowInsets.Companion.none: WindowInsets
    @Stable
    get() = INSTANCE
