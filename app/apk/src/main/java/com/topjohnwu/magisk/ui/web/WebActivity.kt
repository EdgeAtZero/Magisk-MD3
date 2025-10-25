package com.topjohnwu.magisk.ui.web

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.coroutineScope
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.ui.module.ModuleInfo

class WebActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Enable edge to edge
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(EXTRA_ID)
        val name = intent.getStringExtra(EXTRA_NAME)
        val path = intent.getStringExtra(EXTRA_PATH)
        if (id == null || name == null || path == null) {
            finishAndRemoveTask()
            return
        }

        "Magisk | $name"
            .let {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    @Suppress("DEPRECATION")
                    ActivityManager.TaskDescription(it)
                } else {
                    ActivityManager.TaskDescription.Builder().setLabel(it).build()
                }
            }
            .let(::setTaskDescription)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        with(WebView(this)) {
            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val inset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updateLayoutParams<MarginLayoutParams> {
                    leftMargin = inset.left
                    rightMargin = inset.right
                    topMargin = inset.top
                    bottomMargin = inset.bottom
                }
                return@setOnApplyWindowInsetsListener insets
            }
            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
            }
            addJavascriptInterface(WebViewInterface(context, id, path, this, lifecycle.coroutineScope), "ksu")
            setWebViewClient(WebViewClient(context, "$path/webroot"))
            loadUrl("https://mui.kernelsu.org/index.html")
            setContentView(this)
        }
    }

    companion object {

        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_PATH = "path"

        fun launch(context: Context, info: ModuleInfo): Unit =
            launch(context, info.id, info.name, info.path.absolutePath)

        fun launch(context: Context, id: String, name: String, path: String): Unit =
            context.startActivity(
                with(Intent(context, WebActivity::class.java)) {
                    putExtra(EXTRA_ID, id)
                    putExtra(EXTRA_NAME, name)
                    putExtra(EXTRA_PATH, path)
                }
            )

    }

}
