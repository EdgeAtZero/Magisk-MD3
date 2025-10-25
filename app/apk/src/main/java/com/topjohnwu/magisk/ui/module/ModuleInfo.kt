package com.topjohnwu.magisk.ui.module

import android.text.format.Formatter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.topjohnwu.magisk.InternalApi
import com.topjohnwu.magisk.core.AppContext
import com.topjohnwu.magisk.core.repository.NetworkService
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.nio.ExtendedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import timber.log.Timber

class ModuleInfo(
    val path: ExtendedFile,
    private val svc: NetworkService
) {

    var id by mutableStateOf("")
        private set
    var size by mutableStateOf("")
        private set
    var name by mutableStateOf("")
        private set
    var version by mutableStateOf("")
        private set
    var versionCode by mutableStateOf(0)
        private set
    var author by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var updateInfo by mutableStateOf("")
        private set

    var isActionable by mutableStateOf(false)
        private set
    var isEnable by mutableStateOf(false)
        @InternalApi set
    var isRemove by mutableStateOf(false)
        @InternalApi set
    var isOutdated by mutableStateOf<Boolean?>(null)
        @InternalApi set
    var isUpdated by mutableStateOf(false)
        private set
    var isWeb by mutableStateOf(false)
        private set

    private var updateUrl: String? = null

    @InternalApi
    val actionFile = path.getChildFile("action.sh")
    @InternalApi
    val removeFile = path.getChildFile("remove")
    @InternalApi
    val disableFile = path.getChildFile("disable")
    @InternalApi
    val updateFile = path.getChildFile("update")
    @InternalApi
    val webrootFile = path.getChildFile("webroot")

    @OptIn(InternalApi::class)
    suspend fun fetchModuleInfo() {
        try {
            Shell.cmd("dos2unix < $path/module.prop")
                .let { Dispatchers.IO { it.exec() } }
                .out
                .forEach { line ->
                    val prop = line.split("=".toRegex(), 2).map { it.trim() }
                    if (prop.size != 2)
                        return@forEach

                    val key = prop[0]
                    val value = prop[1]
                    if (key.isEmpty() || key[0] == '#')
                        return@forEach

                    Dispatchers.Main {
                        when (key) {
                            "id" -> id = value
                            "name" -> name = value
                            "version" -> version = value
                            "versionCode" -> versionCode = value.toInt()
                            "author" -> author = value
                            "description" -> description = value
                            "updateJson" -> updateUrl = value
                        }
                    }
                }
        } finally {
        }

        if (id.isEmpty()) {
            id = path.name
        }

        if (name.isEmpty()) {
            name = id
        }


        fun ExtendedFile.folderLength(): Long =
            walk().fold(0L) { p0, p1 -> p0 + p1.length() }

        Dispatchers.Main { size = Dispatchers.IO { Formatter.formatFileSize(AppContext, path.folderLength()) } }
        Dispatchers.Main { isActionable = Dispatchers.IO { actionFile.exists() } }
        Dispatchers.Main { isEnable = Dispatchers.IO { !disableFile.exists() } }
        Dispatchers.Main { isRemove = Dispatchers.IO { removeFile.exists() } }
        Dispatchers.Main { isUpdated = Dispatchers.IO { updateFile.exists() } }
        Dispatchers.Main { isWeb = Dispatchers.IO { webrootFile.exists() } }
    }

    @OptIn(InternalApi::class)
    suspend fun fetchUpdateInfo() {
        try {
            val json = updateUrl?.let { Dispatchers.IO { svc.fetchModuleJson(it) } } ?: return
            updateInfo = json.changelog
            isOutdated = json.versionCode > versionCode
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

}
