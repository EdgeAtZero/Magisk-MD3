package me.edgeatzero.compose.util

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

object PaddingValuesTypeConverter : TwoWayConverter<PaddingValues, AnimationVector4D> {

    override val convertToVector = { padding: PaddingValues ->
        AnimationVector4D(
            v1 = padding.start.value,
            v2 = padding.top.value,
            v3 = padding.end.value,
            v4 = padding.bottom.value
        )
    }

    override val convertFromVector = { vector: AnimationVector4D->
        PaddingValues(
            start = vector.v1.dp,
            top = vector.v2.dp,
            end = vector.v3.dp,
            bottom = vector.v4.dp
        )
    }

}
