package me.edgeatzero.compose.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

val PaddingValues.start: Dp
    get() = calculateStartPadding(LayoutDirection.Ltr)

val PaddingValues.top: Dp
    get() = calculateTopPadding()

val PaddingValues.end: Dp
    get() = calculateEndPadding(LayoutDirection.Ltr)

val PaddingValues.bottom: Dp
    get() = calculateBottomPadding()

fun PaddingValues.copy(
    start: Dp = this.start,
    top: Dp = this.top,
    end: Dp = this.end,
    bottom: Dp = this.bottom
): PaddingValues = PaddingValues(start, top, end, bottom)

infix operator fun PaddingValues.plus(other: Dp): PaddingValues = PaddingValues(
    start + other, top + other, end + other, bottom + other
)

infix operator fun PaddingValues.minus(other: Dp): PaddingValues = PaddingValues(
    start - other, top - other, end - other, bottom - other
)

infix operator fun PaddingValues.plus(other: PaddingValues): PaddingValues = PaddingValues(
    start + other.start, top + other.top, end + other.end, bottom + other.bottom
)

infix operator fun PaddingValues.minus(other: PaddingValues): PaddingValues = PaddingValues(
    start - other.start, top - other.top, end - other.end, bottom - other.bottom
)
