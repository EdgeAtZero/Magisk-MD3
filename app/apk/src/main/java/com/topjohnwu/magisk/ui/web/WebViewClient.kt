package com.topjohnwu.magisk.ui.web

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import coil3.ImageLoader
import coil3.executeBlocking
import coil3.request.ImageRequest
import coil3.toBitmap
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.superuser.Shell
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class WebViewClient(
    private val context: Context,
    private val path: String
) : WebViewClient(), DIAware {

    override val di by closestDI(context)

    private val imageLoader by instance<ImageLoader>()

    private val shell get() = Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER).build()

    private val assetLoader by lazy {
        WebViewAssetLoader.Builder()
            .setDomain("mui.kernelsu.org")
            .addPathHandler("/", SuFilePathHandler(context, File(path), shell))
            .build()
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url

        //POC: Handle ksu://icon/[packageName] to serve app icon via WebView
        if (url.scheme.equals("ksu", ignoreCase = true) && url.host.equals("icon", ignoreCase = true)) {
            val packageName = url.path?.substring(1)
            if (!packageName.isNullOrEmpty()) {
                val outputStream = ByteArrayOutputStream()
                val imageRequest = ImageRequest.Builder(context)
                    .data(context.packageManager.getPackageInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES))
                    .build()
                imageLoader.executeBlocking(imageRequest)
                    .image
                    ?.let { it.toBitmap(it.width, it.height) }
                    ?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    ?: return null
                val inputStream = ByteArrayInputStream(outputStream.toByteArray())
                outputStream.close()
                return WebResourceResponse("image/png", null, inputStream)
            }
        }

        return assetLoader.shouldInterceptRequest(url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (view != null && BuildConfig.DEBUG) {
            view.evaluateJavascript(context.assets.open("eruda.min.js").bufferedReader().use { it.readText() }, null)
            view.evaluateJavascript("eruda.init();", null)
        }
    }

}
