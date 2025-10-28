package com.topjohnwu.magisk.ui.action

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.core.ktx.synchronized
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import timber.log.Timber
import java.io.IOException

class ActionViewModel(val id: String, val name: String, app: Application) : AndroidViewModel(app), DIAware {

    constructor(params: ActionParams, app: Application) : this(params.id, params.name, app)

    override val di by closestDI()

    var log = mutableStateListOf<String>()

    val isConnected = MutableStateFlow(false)

    var isExecuting by mutableStateOf(false)
        private set

    var isSuccess by mutableStateOf<Boolean?>(null)
        private set

    private val logs = mutableListOf<String>().synchronized()
    private val console = object : CallbackList<String>() {
        override fun onAddElement(e: String?) {
            e ?: return
            logs.add(e)
            log.add(e)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            isConnected.first { it }
            isExecuting = true
            isSuccess = execute()
            isExecuting = false
        }
    }

    private suspend fun execute(): Boolean =
        try {
            Dispatchers.IO {
                Shell.cmd("run_action \'${id}\'")
                    .to(console, logs)
                    .exec().isSuccess
            }
        } catch (e: IOException) {
            Timber.e(e)
            false
        }

}
