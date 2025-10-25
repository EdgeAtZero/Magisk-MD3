package com.topjohnwu.magisk.ui.web

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil3.ImageLoader
import coil3.executeBlocking
import coil3.request.ImageRequest
import coil3.toBitmap
import com.topjohnwu.magisk.core.ktx.getLabel
import com.topjohnwu.magisk.core.ktx.toast
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.json.JSONObject
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture

class WebViewInterface(
    private val context: Context,
    private val id: String,
    private val path: String,
    private val view: WebView,
    val coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope, DIAware {

    override val di by closestDI(context)

    private val imageLoader by instance<ImageLoader>()

    private val json by instance<Json>()

    private val shell get() = Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER).build()

    @JavascriptInterface
    fun exec(cmd: String): String =
        ShellUtils.fastCmd(shell, cmd)

    @JavascriptInterface
    fun exec(cmd: String, callbackFunc: String): Unit =
        exec(cmd, null, callbackFunc)

    @JavascriptInterface
    fun exec(cmd: String, options: String?, callbackFunc: String) {
        val command = buildString {
            appendOptions(options)
            append(cmd)
        }
        val result = shell.newJob()
            .add(command)
            .to(ArrayList(), ArrayList())
            .exec()
        val stdout = result.out.joinToString(separator = "\n").let(JSONObject::quote)
        val stderr = result.err.joinToString(separator = "\n").let(JSONObject::quote)
        callCallback(result.code, stdout, stderr, callbackFunc)
    }

    @JavascriptInterface
    fun spawn(command: String, args: String, options: String?, callbackFunc: String) {
        val command = buildString {
            appendOptions(options)
            append(command)
            if (args.isNotEmpty()) {
                json.decodeFromString<JsonArray>(args).forEach {
                    append(' ')
                    append(it.jsonPrimitive.content)
                }
            }
        }
        val stdout = object : CallbackList<String>() {
            override fun onAddElement(s: String) {
                emitData("stdout", s.let(JSONObject::quote), callbackFunc)
            }
        }
        val stderr = object : CallbackList<String>() {
            override fun onAddElement(s: String) {
                emitData("stderr", s.let(JSONObject::quote), callbackFunc)
            }
        }
        val shell = shell
        val future = shell.newJob().add(command).to(stdout, stderr).enqueue()
        CompletableFuture.supplyAsync { future.get() }
            .thenAccept { result -> emitExitCode(result.code, callbackFunc) }
            .whenComplete { _, _ -> runCatching { shell.close() } }
    }

    @JavascriptInterface
    fun toast(msg: String): Unit =
        context.toast(msg, Toast.LENGTH_SHORT)

    @JavascriptInterface
    fun fullScreen(enable: Boolean) {
        if (context is Activity) {
            launch(Dispatchers.Main.immediate) {
                val controller = WindowInsetsControllerCompat(context.window, context.window.decorView)
                if (enable) {
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    @JavascriptInterface
    fun moduleInfo(): String =
        json.encodeToString(buildJsonObject { put("id", id); put("moduleDir", path) })

    @JavascriptInterface
    fun listSystemPackages(): String =
        context.packageManager
            .getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .filter { it.applicationInfo?.flags?.let { flag -> flag and ApplicationInfo.FLAG_SYSTEM != 0 } == true }
            .map(PackageInfo::packageName)
            .sorted()
            .let { json.encodeToString(it) }

    @JavascriptInterface
    fun listUserPackages(): String =
        context.packageManager
            .getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .filter { it.applicationInfo?.flags?.let { flag -> flag and ApplicationInfo.FLAG_SYSTEM == 0 } == true }
            .map(PackageInfo::packageName)
            .sorted()
            .let { json.encodeToString(it) }

    @JavascriptInterface
    fun listAllPackages(): String =
        context.packageManager
            .getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .map(PackageInfo::packageName)
            .sorted()
            .let { json.encodeToString(it) }

    @JavascriptInterface
    fun getPackagesInfo(packageNamesJson: String): String =
        Json.decodeFromString<List<String>>(packageNamesJson)
            .map { packageName ->
                val pi = context.packageManager.getPackageInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES)
                val ai = pi.applicationInfo
                buildJsonObject {
                    put("package", packageName)
                    put("versionName", pi.versionName ?: "")
                    val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        pi.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        pi.versionCode
                    }
                    put("versionCode", code)
                    put("appLabel", ai?.getLabel(context.packageManager) ?: "")
                    put("isSystem", ai != null && (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
                    put("uid", ai?.uid)
                }
            }
            .let { json.encodeToString(it) }

    private val packageIconCache = HashMap<String, String>()

    @JavascriptInterface
    fun cacheAllPackageIcons(size: Int) =
        context.packageManager
            .getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .forEach { packageIconCache[it.packageName] = it.iconBase64(size) }

    @JavascriptInterface
    fun getPackagesIcons(packageNamesJson: String, size: Int): String =
        json.decodeFromString<List<String>>(packageNamesJson)
            .map { packageName ->
                buildJsonObject {
                    put("packageName", packageName)
                    val iconBase64 = packageIconCache.getOrPut(packageName) {
                        context.packageManager
                            .getPackageInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES)
                            .iconBase64(size)
                    }
                    put("icon", iconBase64)
                }
            }
            .let { json.encodeToString(it) }

    private fun callCallback(code: Int, stdout: String, stderr: String, callback: String) {
        launch(Dispatchers.Main.immediate) {
            view.evaluateJavascript(
                "(function() { try { ${callback}(${code}, ${stdout}, ${stderr}); } catch(e) { console.error(e); } })(); ",
                null
            )
        }
    }

    private fun emitData(name: String, data: String, callback: String) {
        launch(Dispatchers.Main.immediate) {
            view.evaluateJavascript(
                "(function() { try { ${callback}.${name}.emit('data', ${data}); } catch(e) { console.error('emitData', e); } })(); ",
                null
            )
        }
    }

    private fun emitExitCode(code: Int, callback: String) {
        launch(Dispatchers.Main.immediate) {
            view.evaluateJavascript(
                "(function() { try { ${callback}.emit('exit', ${code}); } catch(e) { console.error(`emitExit error: \${e}`); } })(); ",
                null
            )
        }
    }

    private fun PackageInfo.iconBase64(size: Int): String {
        val outputStream = ByteArrayOutputStream()
        val imageRequest = ImageRequest.Builder(context)
            .data(this)
            .build()
        imageLoader.executeBlocking(imageRequest)
            .image
            ?.toBitmap(size, size)
            ?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            ?: return ""
        val result = "data:image/png;base64," + Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        outputStream.close()
        return result
    }

    private fun StringBuilder.appendOptions(options: String?) {
        options?.let { options ->
            val json = json.decodeFromString<JsonObject>(options)
            json["cwd"]?.jsonPrimitive?.content?.let {
                append("cd $it; ")
            }
            json["env"]?.jsonObject?.forEach { (key, value) ->
                append("export $key=$value; ")
            }
        }
    }

}
