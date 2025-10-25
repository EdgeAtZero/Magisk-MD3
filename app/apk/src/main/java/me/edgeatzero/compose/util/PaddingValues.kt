package me.edgeatzero.compose.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp

val PaddingValues.start: Dp
    get() = calculateStartPadding(LayoutDirection.Ltr)

val PaddingValues.top: Dp
    get() = calculateTopPadding()

val PaddingValues.end: Dp
    get() = calculateEndPadding(LayoutDirection.Ltr)

val PaddingValues.bottom: Dp
    get() = calculateBottomPadding()

@Stable
fun PaddingValues.copy(
    start: Dp = this.start,
    top: Dp = this.top,
    end: Dp = this.end,
    bottom: Dp = this.bottom
): PaddingValues = PaddingValues(start, top, end, bottom)

@Stable
infix operator fun PaddingValues.plus(other: Dp): PaddingValues = PaddingValues(
    start + other, top + other, end + other, bottom + other
)

@Immutable
private class AddedPaddingValues(val first: PaddingValues, val second: Dp) : PaddingValues {

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        first.calculateLeftPadding(layoutDirection) + second

    override fun calculateTopPadding(): Dp =
        first.calculateTopPadding() + second

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        first.calculateRightPadding(layoutDirection) + second


    override fun calculateBottomPadding(): Dp =
        first.calculateBottomPadding() + second

    override fun equals(other: Any?): Boolean {
        if (other !is AddedPaddingValues) return false
        return first == other.first && second == other.second
    }

    override fun hashCode(): Int =
        first.hashCode() * 31 + second.hashCode()

    override fun toString(): String =
        "($first + $second)"

}

@Stable
infix operator fun PaddingValues.minus(other: Dp): PaddingValues =
    PaddingValues(start - other, top - other, end - other, bottom - other)

@Immutable
private class SubtractedPaddingValues(val first: PaddingValues, val second: Dp) : PaddingValues {

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        (first.calculateLeftPadding(layoutDirection) - second).coerceAtLeast(0.dp)

    override fun calculateTopPadding(): Dp =
        (first.calculateTopPadding() - second).coerceAtLeast(0.dp)

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        (first.calculateRightPadding(layoutDirection) - second).coerceAtLeast(0.dp)

    override fun calculateBottomPadding(): Dp =
        (first.calculateBottomPadding() - second).coerceAtLeast(0.dp)

    override fun equals(other: Any?): Boolean {
        if (other !is SubtractedPaddingValues) return false
        return first == other.first && second == other.second
    }

    override fun hashCode(): Int =
        first.hashCode() * 31 + second.hashCode()

    override fun toString(): String =
        "($first - $second)"

}
