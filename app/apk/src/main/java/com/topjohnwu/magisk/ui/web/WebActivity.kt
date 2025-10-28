package com.topjohnwu.magisk.ui.web

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.topjohnwu.magisk.core.BuildConfig
import com.topjohnwu.magisk.ui.module.ModuleInfo

class WebActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Enable edge to edge
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(EXTRA_ID)
        val name = intent.getStringExtra(EXTRA_NAME)
        val path = intent.getStringExtra(EXTRA_PATH)
        val isInjectInsetsCss = intent.getBooleanExtra(EXTRA_IS_INJECT_INSETS_CSS, true)
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
            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
            }
            WebViewOption(context, id, path, this, lifecycle.coroutineScope).let {
                ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                    val inset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    if (isInjectInsetsCss) {
                        it.onInsetsUpdated(inset)
                    } else {
                        updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            leftMargin = inset.left
                            rightMargin = inset.right
                            topMargin = inset.top
                            bottomMargin = inset.bottom
                        }
                    }
                    return@setOnApplyWindowInsetsListener insets
                }
                it.setup()
            }
            loadUrl("https://mui.kernelsu.org/index.html")
            setContentView(this)
        }
    }

    companion object {

        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_PATH = "path"
        const val EXTRA_IS_INJECT_INSETS_CSS = "is_inject_insets_css"

        fun launch(context: Context, info: ModuleInfo, isInjectInsetsCss: Boolean = true): Unit =
            launch(context, info.id, info.name, info.path.absolutePath, isInjectInsetsCss)

        fun launch(context: Context, id: String, name: String, path: String, isInjectInsetsCss: Boolean = true): Unit =
            context.startActivity(
                with(Intent(context, WebActivity::class.java)) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                    addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    putExtra(EXTRA_ID, id)
                    putExtra(EXTRA_NAME, name)
                    putExtra(EXTRA_PATH, path)
                    putExtra(EXTRA_IS_INJECT_INSETS_CSS, isInjectInsetsCss)
                }
            )

    }

}
