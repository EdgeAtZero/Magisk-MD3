package com.topjohnwu.magisk.ui.superuser

import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.os.ProcessCompat
import com.topjohnwu.magisk.core.ktx.getLabel
import com.topjohnwu.magisk.core.model.su.SuPolicy
import java.util.*

class AppInfo(
    private val packageManager: PackageManager,
    val applicationInfo: ApplicationInfo,
    denyPolicy: List<DenyPolicy>,
    suPolicy: SuPolicy?
) {

    val label = applicationInfo.getLabel(packageManager)
    val packageName = applicationInfo.packageName!!
    val uid = applicationInfo.uid

    val packageInfo = packageManager.getPackageInfo(packageName, MATCH_UNINSTALLED_PACKAGES)!!
    var suPolicy by mutableStateOf(suPolicy, referentialEqualityPolicy())
    val processes = SnapshotStateList<ProcessInfo>().apply { addAll(fetchProcesses(denyPolicy)) }

    val isSharedUID = packageInfo.sharedUserId != null
    val isSystemApp = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    val isUserApp = ProcessCompat.isApplicationUid(applicationInfo.uid)

    val isSuperUserActive by derivedStateOf { this.suPolicy?.let { it.policy != SuPolicy.INTERACTIVE } == true }
    val isSuperUser by derivedStateOf { this.suPolicy?.policy == SuPolicy.ALLOW }
    val isNotify by derivedStateOf { this.suPolicy?.notification == true }
    val isLogging by derivedStateOf { this.suPolicy?.logging == true }
    val isDeny by derivedStateOf { processes.any { it.isEnabled } }

    fun copy(
        packageManager: PackageManager = this.packageManager,
        applicationInfo: ApplicationInfo = this.applicationInfo,
        denyPolicy: List<DenyPolicy> = this.processes
            .filter { it.isEnabled }
            .map { DenyPolicy("${it.packageName}\\|${it.name}") },
        suPolicy: SuPolicy? = this.suPolicy
    ) = AppInfo(
        packageManager = packageManager,
        applicationInfo = applicationInfo,
        denyPolicy = denyPolicy,
        suPolicy = suPolicy
    )

    private fun fetchProcesses(denyPolicy: List<DenyPolicy>): Set<ProcessInfo> {
        val flag = MATCH_DISABLED_COMPONENTS or MATCH_UNINSTALLED_PACKAGES or
                GET_ACTIVITIES or GET_SERVICES or GET_RECEIVERS or GET_PROVIDERS
        val packageInfo = try {
            packageManager.getPackageInfo(applicationInfo.packageName, flag)
        } catch (e: Exception) {
            // Exceed binder data transfer limit, parse the package locally
            packageManager.getPackageArchiveInfo(applicationInfo.sourceDir, flag) ?: return emptySet()
        }

        val processSet = TreeSet<ProcessInfo>(compareBy({ it.name }, { it.isIsolated }))
        processSet += packageInfo.activities.toProcessList(denyPolicy)
        processSet += packageInfo.services.toProcessList(denyPolicy)
        processSet += packageInfo.receivers.toProcessList(denyPolicy)
        processSet += packageInfo.providers.toProcessList(denyPolicy)
        return processSet
    }

    private fun createProcess(name: String, pkg: String = applicationInfo.packageName, denyPolicy: List<DenyPolicy>) =
        ProcessInfo(name, pkg, denyPolicy.any { it.process == name })

    private fun Array<out ComponentInfo>?.toProcessList(denyPolicy: List<DenyPolicy>) =
        orEmpty().map { createProcess(it.getProcName(), denyPolicy = denyPolicy) }

    private fun Array<ServiceInfo>?.toProcessList(denyPolicy: List<DenyPolicy>) = orEmpty().map {
        if (it.isIsolated) {
            if (it.useAppZygote) {
                val proc = applicationInfo.processName ?: applicationInfo.packageName
                createProcess("${proc}_zygote", denyPolicy = denyPolicy)
            } else {
                val proc = if (SDK_INT >= Build.VERSION_CODES.Q)
                    "${it.getProcName()}:${it.name}" else it.getProcName()
                createProcess(proc, ISOLATED_MAGIC, denyPolicy)
            }
        } else {
            createProcess(it.getProcName(), denyPolicy = denyPolicy)
        }
    }

    private fun ComponentInfo.getProcName(): String =
        processName ?: applicationInfo.processName ?: applicationInfo.packageName

    private val ServiceInfo.isIsolated get() = (flags and ServiceInfo.FLAG_ISOLATED_PROCESS) != 0

    private val ServiceInfo.useAppZygote get() = (flags and ServiceInfo.FLAG_USE_APP_ZYGOTE) != 0

    data class ProcessInfo(
        val name: String,
        val packageName: String,
        val isEnabled: Boolean
    ) {
        val isIsolated = packageName == ISOLATED_MAGIC
        val isAppZygote = name.endsWith("_zygote")
    }

    companion object {

        const val ISOLATED_MAGIC = "isolated"

    }

}
