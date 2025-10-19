package me.edgeatzero.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

enum class UIMode {
    MOBILE,
    TABLET;

    companion object {

        val current: UIMode @Composable get() = LocalUIMode.current

    }

}

val LocalUIMode = staticCompositionLocalOf<UIMode> {
    error("No UIMode was provided via LocalUIMode")
}

