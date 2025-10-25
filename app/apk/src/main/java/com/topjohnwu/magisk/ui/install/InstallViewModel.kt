package com.topjohnwu.magisk.ui.install

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.core.AppContext
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.core.Const
import com.topjohnwu.magisk.core.repository.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import timber.log.Timber
import java.io.File
import java.io.IOException

class InstallViewModel(app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

    private val svc: NetworkService by instance()

    var method by mutableStateOf<InstallMethod?>(null)

    var patchFile by mutableStateOf<String?>(null)

    val options = mutableStateListOf<InstallOption>()

    var markdown by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch(Dispatchers.Default) { loadMarkdown() }
    }

    private suspend fun loadMarkdown() {
        val file = File(AppContext.cacheDir, "${BuildConfig.APP_VERSION_CODE}.md")
        try {
            markdown = when {
                file.exists() -> Dispatchers.IO { file.readText() }
                Const.Url.CHANGELOG_URL.isEmpty() -> null
                else -> {
                    val str = Dispatchers.IO { svc.fetchString(Const.Url.CHANGELOG_URL) }
                    Dispatchers.IO { file.writeText(str) }
                    str
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.e(e)
        }
    }

}
