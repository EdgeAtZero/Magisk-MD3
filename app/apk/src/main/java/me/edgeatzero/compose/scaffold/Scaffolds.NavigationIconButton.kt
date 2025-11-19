package me.edgeatzero.compose.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import me.edgeatzero.compose.util.onBackPressed

@Composable
fun Scaffolds.NavigationIconButton() {
    IconButton(onClick = onBackPressed) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
    }
}
