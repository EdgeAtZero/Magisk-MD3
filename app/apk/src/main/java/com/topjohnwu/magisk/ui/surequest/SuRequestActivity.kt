package com.topjohnwu.magisk.ui.surequest

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.core.Config
import com.topjohnwu.magisk.core.base.UntrackedActivity
import com.topjohnwu.magisk.core.su.SuCallbackHandler
import com.topjohnwu.magisk.core.su.SuCallbackHandler.REQUEST
import com.topjohnwu.magisk.ui.theme.MagiskTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.android.x.viewmodel.viewModel

open class SuRequestActivity : AppCompatActivity(), UntrackedActivity, DIAware {

    override val di by closestDI()

    private val viewModel: SuRequestViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setHideOverlayWindows(true)
        }
        setTheme(R.style.Foundation_Default)
        super.onCreate(savedInstanceState)
        if (intent.action == Intent.ACTION_VIEW) {
            val action = intent.getStringExtra("action")
            if (action == REQUEST) {
                lifecycleScope.launch(Dispatchers.Default) {
                    if (viewModel.handler.start(intent)) {
                        Dispatchers.Main {
                            viewModel.finishCallback.value = { finish() }
                            viewModel.timer.start()
                            with(ComposeView(this@SuRequestActivity)) {
                                setContentView(
                                    this,
                                    FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        Gravity.CENTER
                                    )
                                )
                                setContent {
                                    MagiskTheme {
                                        SuRequestDialog(viewModel = viewModel)
                                    }
                                }
                                if (Config.suTapjack) {
                                    ViewCompat.setAccessibilityDelegate(
                                        this,
                                        SuRequestViewModel.EmptyAccessibilityDelegate
                                    )
                                }
                            }
                        }
                    } else {
                        Dispatchers.Main { finish() }
                    }
                }
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    SuCallbackHandler.run(this@SuRequestActivity, action, intent.extras)
                    Dispatchers.Main {
                        finish()
                    }
                }
            }
        } else {
            finish()
        }
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(R.style.Foundation_Floating, true)
        return theme
    }

    override fun finish() {
        super.finishAndRemoveTask()
    }

}
