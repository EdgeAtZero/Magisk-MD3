package com.topjohnwu.magisk.ui.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.core.ktx.await
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

class HomeViewModel(app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

    var envCheckCode by mutableStateOf(0)
        private set

    init {
        viewModelScope.launch {
            envCheckCode = ensureEnv()
        }
    }

    private suspend fun ensureEnv(): Int {
        if (!Info.env.isActive) return 0
        val cmd = "env_check ${Info.env.versionString} ${Info.env.versionCode}"
        return Shell.cmd(cmd).await().code
    }

}
