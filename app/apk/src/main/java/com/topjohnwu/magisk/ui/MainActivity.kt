package com.topjohnwu.magisk.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.arch.ViewModelHolder
import com.topjohnwu.magisk.arch.viewModel
import com.topjohnwu.magisk.core.base.ActivityExtension
import com.topjohnwu.magisk.core.base.SplashController
import com.topjohnwu.magisk.core.base.SplashScreenHost
import com.topjohnwu.magisk.ui.home.HomeViewModel
import com.topjohnwu.magisk.ui.nav.MainNavigation
import com.topjohnwu.magisk.ui.theme.MagiskTheme
import me.edgeatzero.compose.util.LocalUIMode
import me.edgeatzero.compose.util.UIMode


class MainActivity : ComponentActivity(), SplashScreenHost, ViewModelHolder {

    override val splashController = SplashController(this)
    override val extension = ActivityExtension(this)

    override val viewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        splashController.preOnCreate()
        super.onCreate(savedInstanceState)
        splashController.onCreate(savedInstanceState)
        viewModel.startLoading()
    }

    override fun onCreateUi(savedInstanceState: Bundle?) {
        setContent {
            MagiskTheme {
                BoxWithConstraints {
                    CompositionLocalProvider(
                        LocalUIMode provides if (maxWidth >= 600.dp) UIMode.TABLET else UIMode.MOBILE
                    ) {
                        MainNavigation()
                    }
                }
            }
        }
    }

    override fun showInvalidStateMessage() {
    }

}
