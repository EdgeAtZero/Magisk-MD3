package com.topjohnwu.magisk.ui.module

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.TaskStackBuilder
import com.topjohnwu.magisk.InternalApi
import com.topjohnwu.magisk.core.Info
import com.topjohnwu.magisk.core.download.Subject
import com.topjohnwu.magisk.core.model.module.OnlineModule
import com.topjohnwu.magisk.core.repository.NetworkService
import com.topjohnwu.magisk.ui.MainActivity
import com.topjohnwu.magisk.ui.navigation.MainDestination
import com.topjohnwu.magisk.ui.navigation.build
import com.topjohnwu.magisk.view.Notifications
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.nio.ExtendedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@OptIn(InternalApi::class)
class ModuleInfo(
    val path: ExtendedFile,
    private val svc: NetworkService
) {

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

    @InternalApi
    val zygiskFolder = path.getChildFile("zygisk")

    var id by mutableStateOf("")
        private set
    var size by mutableStateOf(0L)
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
    var updateVersion by mutableStateOf("")
        private set
    var updateVersionCode by mutableStateOf(0)
        private set

    var isActionable by mutableStateOf(false)
        private set
    var isEnable by mutableStateOf(false)
        @InternalApi set
    var isRemove by mutableStateOf(false)
        @InternalApi set
    var isOutdated by mutableStateOf<Boolean?>(null)
        @InternalApi set
    val isRiru = (id == "riru-core") || path.getChildFile("riru").exists()
    var isUpdated by mutableStateOf(false)
        private set
    var isWeb by mutableStateOf(false)
        private set
    val isZygisk = zygiskFolder.exists()
    val isZygiskUnloaded = zygiskFolder.getChildFile("unloaded").exists()

    private var updateUrl: String? = null
    private var zipFileUrl: String? = null

    val notice by derivedStateOf {
        when {
            isZygisk && isZygiskUnloaded -> "存在兼容性问题，此模块未加载"
            isZygisk && !Info.isZygiskEnabled -> "Zygisk 未启用，此模块暂停加载"
            isRiru && Info.isZygiskEnabled -> "Zygisk 已启用，此模块暂停加载"
            else -> null
        }
    }

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

        size = Dispatchers.IO { path.folderLength() }
        isActionable = Dispatchers.IO { actionFile.exists() }
        isEnable = Dispatchers.IO { !disableFile.exists() }
        isRemove = Dispatchers.IO { removeFile.exists() }
        isUpdated = Dispatchers.IO { updateFile.exists() }
        isWeb = Dispatchers.IO { webrootFile.exists() }
    }

    @OptIn(InternalApi::class)
    suspend fun fetchUpdateInfo() {
        try {
            val json = updateUrl?.let { Dispatchers.IO { svc.fetchModuleJson(it) } } ?: return
            updateVersion = json.version
            updateVersionCode = json.versionCode
            isOutdated = updateVersionCode > versionCode
            updateInfo = if (isOutdated == true) Dispatchers.IO { svc.fetchString(json.changelog) } else ""
            zipFileUrl = json.zipUrl
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

    fun toSubject(autoLaunch: Boolean = true) =
        Subject(module = toOnlineModule(), autoLaunch = autoLaunch)

    fun toOnlineModule(): OnlineModule =
        OnlineModule(
            id = this.id,
            name = this.name,
            version = this.updateVersion,
            versionCode = this.updateVersionCode,
            zipUrl = checkNotNull(zipFileUrl),
            changelog = updateInfo
        )

    @Parcelize
    data class Subject(
        override val module: OnlineModule,
        override val autoLaunch: Boolean,
        override val notifyId: Int = Notifications.nextId()
    ) : Subject.Module() {

        override fun pendingIntent(context: Context): PendingIntent? =
            with(TaskStackBuilder.create(context)) {
                addNextIntentWithParentStack(
                    Intent(
                        Intent.ACTION_VIEW,
                        MainDestination.Flash.deeplink.build(
                            MainDestination.Flash(file.toString()),
                            MainDestination.Flash.typeMap
                        ).let(Uri::parse),
                        context,
                        MainActivity::class.java
                    )
                )
                getPendingIntent(
                    System.currentTimeMillis().toInt(),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

    }

}
