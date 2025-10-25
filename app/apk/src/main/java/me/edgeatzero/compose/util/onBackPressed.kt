package me.edgeatzero.compose.util

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable

val onBackPressed: () -> Unit
    @Composable
    get() = LocalOnBackPressedDispatcherOwner.current?.let { it.onBackPressedDispatcher::onBackPressed } ?: {}
