package com.topjohnwu.magisk.ui.superuser

import android.app.Application
import android.content.pm.PackageManager
import android.os.Process
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.core.AppContext
import com.topjohnwu.magisk.core.data.magiskdb.PolicyDao
import com.topjohnwu.magisk.core.model.su.SuPolicy
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class SuperUserViewModel(app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

    private val db: PolicyDao by instance()

    var isRefreshing by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val mutex = Mutex()
    private val _apps = mutableStateListOf<AppInfo>()

    var filter by mutableStateOf<AppFilter>(AppFilter.USER)
    var priorities by mutableStateOf<List<AppPriority>>(AppPriorities)
    var searchText by mutableStateOf("")
    var sort by mutableStateOf<AppSort>(AppSort.Name)

    val apps by derivedStateOf {
        _apps
            .filter {
                when (filter) {
                    AppFilter.ALL -> true
                    AppFilter.USER -> !it.isSystemApp
                    AppFilter.SYSTEM -> it.isSystemApp
                }
            }
            .filter {
                it.label.contains(searchText, true) || it.packageName.contains(searchText, true)
            }
            .sortedWith { p0, p1 ->
                priorities
                    .fold(0) { acc, priority ->
                        if (acc != 0) return@sortedWith acc
                        priority.compare(p0, p1)
                    }
                    .takeIf { it != 0 }
                    ?: sort.compare(p0, p1)
            }
            .also { badge = it.count { item -> item.isSuperUser } }
    }

    init {
        isLoading = true
        viewModelScope.launch(Dispatchers.Default) {
            mutex.withLock { loadApps() }
            isLoading = false
        }
    }

    fun refresh() {
        isRefreshing = true
        viewModelScope.launch(Dispatchers.Default) {
            mutex.withLock { loadApps() }
            isRefreshing = false
        }
    }

    fun updateSuPolicy(
        item: AppInfo,
        enable: Boolean? = null,
        logging: Boolean? = null,
        notification: Boolean? = null
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val policy = if (enable == null && logging == null && notification == null) {
                null
            } else {
                SuPolicy(
                    item.applicationInfo.uid,
                    enable?.let { if (it) SuPolicy.ALLOW else SuPolicy.DENY } ?: SuPolicy.ALLOW,
                    item.suPolicy?.remain ?: 0,
                    logging ?: item.suPolicy?.logging ?: true,
                    notification ?: item.suPolicy?.notification ?: true
                )
            }
            if (policy == null) {
                db.delete(item.uid)
            } else {
                db.update(policy)
            }
            mutex.withLock {
                item.suPolicy = policy
                _apps.forEach {
                    if (it.uid == item.uid) {
                        it.suPolicy = policy
                    }
                }
            }
        }
    }

    fun updateDenyPolicy(item: AppInfo, enable: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val index = _apps.indexOf(item)
            if (index != -1) {
                mutex.withLock {
                    item.processes.forEachIndexed { index, process ->
                        updateDenyPolicy(index, item, process, enable)
                    }
                }
            }
        }
    }

    fun updateDenyPolicy(item: AppInfo, process: AppInfo.ProcessInfo, enable: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val index = item.processes.indexOf(process)
            if (index == -1) return@launch
            mutex.withLock { updateDenyPolicy(index, item, process, enable) }
        }
    }

    private suspend fun updateDenyPolicy(index: Int, item: AppInfo, process: AppInfo.ProcessInfo, enable: Boolean) {
        val arg = if (enable) "add" else "rm"
        val (name, pkg) = process
        Dispatchers.IO { Shell.cmd("magisk --denylist $arg $pkg \'$name\'").submit() }
        item.processes[index] = process.copy(isEnabled = enable)
    }

    private suspend fun loadApps() {
        _apps.clear()
        val pm = application.packageManager
        val denyList = getDenyList()
        val suList = pm.getSuList()
        pm
            .getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .filter { item -> AppContext.packageName != item.packageName }
            .map { item ->
                AppInfo(
                    packageManager = pm,
                    applicationInfo = item,
                    denyPolicy = denyList.filter { it.packageName == item.packageName },
                    suPolicy = suList.firstOrNull { it.uid == item.uid })
            }
            .let { _apps.addAll(it) }
    }

    private suspend fun getDenyList(): List<DenyPolicy> = Dispatchers.IO {
        Shell.cmd("magisk --denylist ls")
            .exec()
            .out
            .map { DenyPolicy(it) }
    }

    private suspend fun PackageManager.getSuList(): List<SuPolicy> =
        db.fetchAll()
            .filter { policy ->
                val pkgs = if (policy.uid == Process.SYSTEM_UID) {
                    arrayOf("android")
                } else {
                    getPackagesForUid(policy.uid)
                }
                if (pkgs == null) {
                    Dispatchers.IO { db.delete(policy.uid) }
                    return@filter false
                }
                val isUninstalled = pkgs.none { pkg ->
                    try {
                        getPackageInfo(pkg, PackageManager.MATCH_UNINSTALLED_PACKAGES)
                        true
                    } catch (e: PackageManager.NameNotFoundException) {
                        false
                    }
                }
                if (isUninstalled) {
                    Dispatchers.IO { db.delete(policy.uid) }
                    return@filter false
                }
                return@filter true
            }

    companion object {

        var badge by mutableStateOf(-1)
            private set

    }

}
