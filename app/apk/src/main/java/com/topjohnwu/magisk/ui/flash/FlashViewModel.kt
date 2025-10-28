package com.topjohnwu.magisk.ui.flash

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.core.ktx.synchronized
import com.topjohnwu.magisk.core.tasks.FlashZip
import com.topjohnwu.magisk.core.tasks.MagiskInstaller
import com.topjohnwu.magisk.ui.install.InstallMethod
import com.topjohnwu.superuser.CallbackList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

class FlashViewModel(private val params: FlashParams, app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

    var log = mutableStateListOf<String>()

    val isConnected = MutableStateFlow(false)

    var isFlashing by mutableStateOf(false)
        private set

    var isSuccess by mutableStateOf<Boolean?>(null)
        private set

    val isRebootAvailable by derivedStateOf { isShouldReboot && !isFlashing && isSuccess == true }

    private val logs = mutableListOf<String>().synchronized()
    private val console = object : CallbackList<String>() {
        override fun onAddElement(e: String?) {
            e ?: return
            logs.add(e)
            log.add(e)
        }
    }

    private var isShouldReboot by mutableStateOf(true)

    init {
        viewModelScope.launch(Dispatchers.Default) {
            isConnected.first { it }
            isFlashing = true
            isSuccess = flash()
            isFlashing = false
        }
    }

    private suspend fun flash(): Boolean = when (params) {
        is FlashParams.Install -> when (params.method) {
            InstallMethod.Patch -> {
                isShouldReboot = false
                val uri = params.patchFile?.let { Uri.parse(it) } ?: error("Patch file uri is null")
                Dispatchers.IO { MagiskInstaller.Patch(uri, console, logs).exec() }
            }

            InstallMethod.Direct -> if (Info.isEmulator) {
                Dispatchers.IO { MagiskInstaller.Emulator(console, logs).exec() }
            } else {
                Dispatchers.IO { MagiskInstaller.Direct(console, logs).exec() }
            }

            InstallMethod.InactiveSlot -> {
                isShouldReboot = false
                Dispatchers.IO { MagiskInstaller.SecondSlot(console, logs).exec() }
            }
        }

        is FlashParams.Module -> {
            Dispatchers.IO { FlashZip(Uri.parse(params.file), console, logs).exec() }
        }

        FlashParams.Uninstall -> {
            isShouldReboot = false
            Dispatchers.IO { MagiskInstaller.Uninstall(console, logs).exec() }
        }
    }

}
