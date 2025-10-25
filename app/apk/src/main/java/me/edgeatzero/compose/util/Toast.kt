package me.edgeatzero.compose.util

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch

@JvmInline
value class ToastAction(private val backend: (args: Array<out String>) -> Unit) {
    operator fun invoke() = backend(emptyArray<String>())
    operator fun invoke(vararg args: String) = backend(args)
}


@Composable
fun rememberToastAction(
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
    onResult: (() -> Unit)? = null
): SnackbarAction {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    return remember {
        SnackbarAction { args ->
            coroutineScope.launch {
                val toast = Toast.makeText(context, message.format(*args), duration)
                if (onResult != null) {
                    toast.addCallback(
                        object : Toast.Callback() {
                            override fun onToastHidden(): Unit = onResult()
                        }
                    )
                }
                Dispatchers.Main {
                    toast.show()
                }
            }
        }
    }
}
