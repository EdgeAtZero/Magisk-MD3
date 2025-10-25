package me.edgeatzero.android

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract

class ActivityResultHandler<I, O>(
    activity: ComponentActivity,
    contact: ActivityResultContract<I, O>
) {

    private var callback: ((O) -> Unit)? = null
        @Synchronized get

    private val launcher = activity.registerForActivityResult(contact) { callback?.invoke(it) }

    fun launch(input: I, callback: (O) -> Unit) {
        this.callback = callback
        launcher.launch(input)
    }

}
