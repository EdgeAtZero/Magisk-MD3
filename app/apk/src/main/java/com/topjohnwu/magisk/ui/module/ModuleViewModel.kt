package com.topjohnwu.magisk.ui.module

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.InternalApi
import com.topjohnwu.magisk.core.Const
import com.topjohnwu.magisk.core.model.module.LocalModule
import com.topjohnwu.magisk.core.repository.NetworkService
import com.topjohnwu.magisk.core.utils.RootUtils
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class ModuleViewModel(app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

    private val svc: NetworkService by instance()

    var isRefreshing by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val mutex = Mutex()
    private val _modules = mutableStateListOf<ModuleInfo>()

    val modules by derivedStateOf {
        _modules.sortedBy { it.name }
    }

    val badge by derivedStateOf { _modules.size.toString().takeUnless { isLoading || isRefreshing } }

    init {
        isLoading = true
        viewModelScope.launch(Dispatchers.Default) {
            mutex.withLock {
                loadModules()
                isLoading = false
                _modules.forEach { it.fetchUpdateInfo() }
            }
        }
    }

    fun refresh() {
        isRefreshing = true
        viewModelScope.launch(Dispatchers.Default) {
            mutex.withLock {
                loadModules()
                isRefreshing = false
                _modules.forEach { it.fetchUpdateInfo() }
            }
        }
    }

    @OptIn(InternalApi::class)
    fun updateModule(module: ModuleInfo, enable: Boolean? = null, remove: Boolean? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            if (enable != null) {
                if (enable) {
                    module.disableFile.delete()
                } else {
                    module.disableFile.createNewFile()
                }
                Shell.cmd("copy_preinit_files").submit()
                Dispatchers.Main { module.isEnable = enable }
            }
            if (remove != null && !module.isUpdated) {
                if (remove) {
                    module.removeFile.createNewFile()
                } else {
                    module.removeFile.delete()
                }
                Shell.cmd("copy_preinit_files").submit()
                Dispatchers.Main { module.isRemove = remove }
            }
        }
    }

    private suspend fun loadModules() {
        if (LocalModule.loaded()) {
            _modules.clear()
            Dispatchers.IO {
                RootUtils.fs.getFile(Const.MODULE_PATH)
                    .listFiles()
                    .orEmpty()
                    .filter { !it.isFile && !it.isHidden }
                    .map { ModuleInfo(path = it, svc = svc).apply { fetchModuleInfo() } }
                    .let { Dispatchers.Main { _modules.addAll(it) } }
            }
        }
    }

}
