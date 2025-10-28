package com.topjohnwu.magisk.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.core.base.ActivityExtension
import com.topjohnwu.magisk.core.base.SplashController
import com.topjohnwu.magisk.core.base.SplashScreenHost
import com.topjohnwu.magisk.ui.navigation.MainNavigation
import com.topjohnwu.magisk.ui.theme.MagiskTheme
import me.edgeatzero.compose.util.ProvideUIMode
import me.edgeatzero.compose.util.UIMode

class MainActivity : AppCompatActivity(), SplashScreenHost {

    override val splashController = SplashController(this)
    override val extension = ActivityExtension(this)

    private var shouldShowInvalidStateMessage by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Foundation_Default)
        splashController.preOnCreate()
        super.onCreate(savedInstanceState)
        splashController.onCreate(savedInstanceState)
    }

    override fun onCreateUi(savedInstanceState: Bundle?) {
        setContent {
            MagiskTheme {
                BoxWithConstraints {
                    ProvideUIMode(if (maxWidth >= 600.dp) UIMode.TABLET else UIMode.MOBILE) {
                        MainNavigation()
                    }
                }
            }
        }
    }

    override fun showInvalidStateMessage() {
        shouldShowInvalidStateMessage = true
    }

}
