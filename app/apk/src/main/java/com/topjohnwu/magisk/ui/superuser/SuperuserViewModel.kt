package com.topjohnwu.magisk.ui.superuser

import android.content.pm.PackageManager
import android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES
import android.os.Process
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.magisk.core.AppContext
import com.topjohnwu.magisk.core.data.magiskdb.PolicyDao
import com.topjohnwu.magisk.core.di.ServiceLocator
import com.topjohnwu.magisk.core.model.su.SuPolicy
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch

class SuperUserViewModel(private val db: PolicyDao = ServiceLocator.policyDB) : ViewModel() {

    enum class FilterApps { ALL, SYSTEM, USER }

    var isLoading by mutableStateOf(true)
        private set

    private val _apps = mutableStateListOf<AppInfo>()

    var filterApps by mutableStateOf(FilterApps.ALL)
    var searchText by mutableStateOf("")

    val apps by derivedStateOf {
        if (isLoading) return@derivedStateOf emptyList()
        _apps
            .filter {
                when (filterApps) {
                    FilterApps.ALL -> true
                    FilterApps.USER -> !it.isSystemApp
                    FilterApps.SYSTEM -> it.isSystemApp
                }
            }
            .filter {
                it.label.contains(searchText) || it.packageName.contains(searchText)
            }
            .sortedWith(compareByDescending<AppInfo> { it.isSuperUserActive }.thenBy { it.label })
    }

    fun deleteSuperUser(item: AppInfo) {
        viewModelScope.launch {
            db.delete(item.uid)
            _apps.forEach {
                if (it.uid == item.uid) {
                    it.suPolicy = null
                }
            }
        }
    }

    fun updateSuperUser(item: AppInfo, enable: Boolean) {
        viewModelScope.launch {
            val policy = item.suPolicy?.apply { policy = if (enable) SuPolicy.ALLOW else SuPolicy.DENY }
                ?: SuPolicy(item.applicationInfo.uid, SuPolicy.ALLOW, 0, true, true)
            db.update(policy)
            _apps.forEach {
                if (it.uid == item.uid) {
                    it.suPolicy = policy
                }
            }
        }
    }

    fun updateDeny(item: AppInfo, enable: Boolean) {
        viewModelScope.launch {
            val index = _apps.indexOf(item)
            if (index == -1) return@launch
            item.processes.forEachIndexed { index1, processInfo ->
                updateDeny(index1, item, processInfo, enable)
            }
        }
    }

    fun updateDeny(item: AppInfo, processInfo: AppInfo.ProcessInfo, enable: Boolean) {
        viewModelScope.launch {
            val index = item.processes.indexOf(processInfo)
            if (index == -1) return@launch
            updateDeny(index, item, processInfo, enable)
        }
    }

    private suspend fun updateDeny(index: Int, item: AppInfo, processInfo: AppInfo.ProcessInfo, enable: Boolean) {
        val arg = if (enable) "add" else "rm"
        val (name, pkg) = processInfo
        Shell.cmd("magisk --denylist $arg $pkg \'$name\'").submit()
        item.processes[index] = processInfo.copy(isEnabled = enable)
    }

    fun updateNotify(item: AppInfo, enable: Boolean) {
        viewModelScope.launch {
            val policy = item.suPolicy?.apply { notification = enable } ?: return@launch
            db.update(policy)
            _apps.forEach {
                if (it.uid == item.uid) {
                    it.suPolicy = policy
                }
            }
        }
    }

    fun updateLogging(item: AppInfo, enable: Boolean) {
        viewModelScope.launch {
            val policy = item.suPolicy?.apply { logging = enable } ?: return@launch
            db.update(policy)
            _apps.forEach {
                if (it.uid == item.uid) {
                    it.suPolicy = policy
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            loadApps()
            isLoading = false
        }
    }

    fun refreshApps() {
        isLoading = true
        viewModelScope.launch {
            loadApps()
            isLoading = false
        }
    }

    private suspend fun loadApps() {
        _apps.clear()
        val pm = AppContext.packageManager
        val denyList = getDenyList()
        val suList = getSuList()
        _apps.addAll(
            Dispatchers.Default {
                pm
                    .getInstalledApplications(MATCH_UNINSTALLED_PACKAGES)
                    .filter { item -> AppContext.packageName != item.packageName }
                    .map { item ->
                        AppInfo(
                            packageManager = pm,
                            applicationInfo = item,
                            denyPolicy = denyList.filter { it.packageName == item.packageName },
                            suPolicy = suList.firstOrNull { it.uid == item.uid })
                    }
                    .sortedBy { it.label }
            }
        )
    }

    private suspend fun getDenyList(): List<DenyPolicy> = Dispatchers.Default {
        Shell
            .cmd("magisk --denylist ls")
            .exec()
            .out
            .map { DenyPolicy(it) }
    }

    private suspend fun getSuList(): List<SuPolicy> = Dispatchers.IO {
        val pm = AppContext.packageManager
        db.fetchAll().filter { policy ->
            val pkgs = if (policy.uid == Process.SYSTEM_UID) {
                arrayOf("android")
            } else {
                pm.getPackagesForUid(policy.uid)
            }
            if (pkgs == null) {
                db.delete(policy.uid)
                return@filter false
            }
            val isUninstalled = pkgs.none { pkg ->
                try {
                    pm.getPackageInfo(pkg, MATCH_UNINSTALLED_PACKAGES)
                    true
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }
            if (isUninstalled) {
                db.delete(policy.uid)
                return@filter false
            }
            return@filter true
        }
    }

}
