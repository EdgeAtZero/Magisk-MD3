@file:Suppress("UNUSED")

package com.topjohnwu.magisk.ui.web

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.webkit.*
import android.widget.Toast
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebViewAssetLoader
import coil3.ImageLoader
import coil3.executeBlocking
import coil3.request.ImageRequest
import coil3.toBitmap
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.core.ktx.getLabel
import com.topjohnwu.magisk.core.ktx.toast
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.json.JSONObject
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt

class WebViewOption(
    private val id: String,
    private val path: String,
    private val view: WebView,
    private val coroutineScope: CoroutineScope,
    private val shell: Shell,
    private val context: Context = view.context
) : CoroutineScope by coroutineScope, DIAware {

    override val di by closestDI(context)

    private val imageLoader by instance<ImageLoader>()

    private val json by instance<Json>()

    private val assetLoader by lazy {
        WebViewAssetLoader.Builder()
            .setDomain("mui.kernelsu.org")
            .addPathHandler("/", SuFilePathHandler(context, File("$path/webroot"), shell))
            .build()
    }


    private val isConnected = MutableStateFlow(false)

    val client by lazy { Client() }

    val `interface` by lazy { Interface() }

    fun setup() {
        view.addJavascriptInterface(`interface`, NAME)
        view.setWebViewClient(client)
    }

    inner class Client : WebViewClient() {

        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            val url = request.url

            //POC: Handle ksu://icon/[packageName] to serve app icon via WebView
            if (url.scheme.equals(NAME, ignoreCase = true) && url.host.equals("icon", ignoreCase = true)) {
                val packageName = url.path?.substring(1)
                if (!packageName.isNullOrEmpty()) {
                    return context.packageManager
                        .getPackageInfo(packageName, MATCH_UNINSTALLED_PACKAGES)
                        .iconBytes()
                        .let { WebResourceResponse("image/png", null, ByteArrayInputStream(it)) }
                }
            }

            return assetLoader.shouldInterceptRequest(url)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (view != null && BuildConfig.DEBUG) {
                view.evaluateJavascript(
                    context.assets.open("eruda.min.js").bufferedReader().use { it.readText() },
                    null
                )
                view.evaluateJavascript("eruda.init();", null)
            }
            isConnected.value = true
        }

    }

    inner class Interface {

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
            CompletableFuture.supplyAsync { shell.newJob().add(command).to(stdout, stderr).enqueue().get() }
                .thenAccept { result -> emitExitCode(result.code, callbackFunc) }
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
                .getInstalledPackages(MATCH_UNINSTALLED_PACKAGES)
                .filter { it.applicationInfo?.flags?.let { flag -> flag and ApplicationInfo.FLAG_SYSTEM != 0 } == true }
                .map(PackageInfo::packageName)
                .sorted()
                .let { json.encodeToString(it) }

        @JavascriptInterface
        fun listUserPackages(): String =
            context.packageManager
                .getInstalledPackages(MATCH_UNINSTALLED_PACKAGES)
                .filter { it.applicationInfo?.flags?.let { flag -> flag and ApplicationInfo.FLAG_SYSTEM == 0 } == true }
                .map(PackageInfo::packageName)
                .sorted()
                .let { json.encodeToString(it) }

        @JavascriptInterface
        fun listAllPackages(): String =
            context.packageManager
                .getInstalledPackages(MATCH_UNINSTALLED_PACKAGES)
                .map(PackageInfo::packageName)
                .sorted()
                .let { json.encodeToString(it) }

        @JavascriptInterface
        fun getPackagesInfo(packageNamesJson: String): String =
            Json.decodeFromString<List<String>>(packageNamesJson)
                .map { packageName ->
                    val pi = context.packageManager.getPackageInfo(packageName, MATCH_UNINSTALLED_PACKAGES)
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

        @JavascriptInterface
        fun cacheAllPackageIcons(size: Int) =
            context.packageManager
                .getInstalledPackages(MATCH_UNINSTALLED_PACKAGES)
                .forEach { it.iconBytes() }

        @JavascriptInterface
        fun getPackagesIcons(packageNamesJson: String, size: Int): String =
            json.decodeFromString<List<String>>(packageNamesJson)
                .map { packageName ->
                    buildJsonObject {
                        put("packageName", packageName)
                        val pi = context.packageManager.getPackageInfo(packageName, MATCH_UNINSTALLED_PACKAGES)
                        put("icon", pi.iconBase64())
                    }
                }
                .let { json.encodeToString(it) }

    }

    fun onInsetsUpdated(insets: Insets) {
        launch(Dispatchers.Default) {
            isConnected.first { it }
            Dispatchers.Main.immediate {
                view.evaluateJavascript(
                    "(function() {" +
                            // 移除之前添加的样式
                            "   var oldStyle = document.getElementById('insets-injected-style');" +
                            "   if (oldStyle) {" +
                            "       oldStyle.remove();" +
                            "   }" +
                            // 创建新的样式标签
                            "   var style = document.createElement('style');" +
                            "   style.id = 'insets-injected-style';" +
                            "   style.type = 'text/css';" +
                            "   style.innerHTML = `" + insets.css() + "`;" +
                            // 添加到 head
                            "   document.head.appendChild(style);" +
                            "})();",
                    null
                )
            }
        }
    }

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

    private fun Insets.css() =
        buildString {
            appendLine(":root {")
            appendLine("\t--safe-area-inset-top: ${top.px2dp()}px;")
            appendLine("\t--safe-area-inset-right: ${right.px2dp()}px;")
            appendLine("\t--safe-area-inset-bottom: ${bottom.px2dp()}px;")
            appendLine("\t--safe-area-inset-left: ${left.px2dp()}px;")
            appendLine("\t--window-inset-top: var(--safe-area-inset-top, 0px);")
            appendLine("\t--window-inset-bottom: var(--safe-area-inset-bottom, 0px);")
            appendLine("\t--window-inset-left: var(--safe-area-inset-left, 0px);")
            appendLine("\t--window-inset-right: var(--safe-area-inset-right, 0px);")
            appendLine("\t--f7-safe-area-top: var(--window-inset-top, 0px) !important;")
            appendLine("\t--f7-safe-area-bottom: var(--window-inset-bottom, 0px) !important;")
            appendLine("\t--f7-safe-area-left: var(--window-inset-left, 0px) !important;")
            appendLine("\t--f7-safe-area-right: var(--window-inset-right, 0px) !important;")
            append("}")
        }

    private fun Int.px2dp(): Int =
        (this / context.resources.displayMetrics.density).roundToInt()

    private fun Int.dp2px(): Int =
        (this * context.resources.displayMetrics.density).roundToInt()

    private val packageIconCache = HashMap<String, ByteArray>()

    private fun PackageInfo.iconBase64(): String =
        "data:image/png;base64," + Base64.encodeToString(iconBytes(), Base64.NO_WRAP)

    private fun PackageInfo.iconBytes(): ByteArray =
        packageIconCache.getOrPut(packageName) {
            val outputStream = ByteArrayOutputStream()
            val imageRequest = ImageRequest.Builder(context)
                .data(this)
                .build()
            imageLoader.executeBlocking(imageRequest)
                .image
                ?.let { it.toBitmap(it.width, it.height) }
                ?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                ?: throw IllegalStateException("can't get the icon")
            val result = outputStream.toByteArray()
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

    companion object {
        const val NAME = "ksu"
    }

}
