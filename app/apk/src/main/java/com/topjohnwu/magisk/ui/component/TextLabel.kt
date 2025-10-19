package com.topjohnwu.magisk.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextLabel(text: String) {
    Box(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.primaryContainer,
            RoundedCornerShape(25)
        )
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp),
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, lineHeight = 8.sp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
