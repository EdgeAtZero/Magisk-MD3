package me.edgeatzero.compose.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.launch

val LocalSnackbarState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState was provided via LocalSnackbarState")
}

@JvmInline
value class SnackbarAction(private val backend: (args: Array<out String>) -> Unit) {
    operator fun invoke() = backend(emptyArray<String>())
    operator fun invoke(vararg args: String) = backend(args)
}

@Composable
fun rememberSnackbarAction(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    onResult: suspend (SnackbarResult) -> Unit = {}
): SnackbarAction {
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = LocalSnackbarState.current
    return remember {
        SnackbarAction { args ->
            coroutineScope.launch {
                val result = snackbarState
                    .showSnackbar(message.format(*args), actionLabel, withDismissAction, duration)
                onResult(result)
            }
        }
    }
}

@Composable
fun rememberSnackbarAction(
    visuals: SnackbarVisuals,
    onResult: suspend (SnackbarResult) -> Unit = {}
): SnackbarAction {
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = LocalSnackbarState.current
    return remember {
        SnackbarAction { args ->
            coroutineScope.launch {
                val proxiedVisuals = object : SnackbarVisuals by visuals {
                    override val message = visuals.message.format(args)
                }
                val result = snackbarState.showSnackbar(proxiedVisuals)
                onResult(result)
            }
        }
    }
}
