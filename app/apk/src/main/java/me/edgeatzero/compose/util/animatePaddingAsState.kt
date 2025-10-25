package me.edgeatzero.compose.util

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.dp

@SuppressLint("ComposableNaming")
@Composable
fun animatePaddingAsState(
    targetValue: PaddingValues,
    animationSpec: AnimationSpec<PaddingValues> = spring(),
    visibilityThreshold: PaddingValues = PaddingValues(1.dp),
    label: String = "FloatAnimation",
    finishedListener: ((PaddingValues) -> Unit)? = null,
): State<PaddingValues> = animateValueAsState(
    targetValue = targetValue,
    typeConverter = PaddingValuesTypeConverter,
    animationSpec = animationSpec,
    visibilityThreshold = visibilityThreshold,
    label = label,
    finishedListener = finishedListener
)
