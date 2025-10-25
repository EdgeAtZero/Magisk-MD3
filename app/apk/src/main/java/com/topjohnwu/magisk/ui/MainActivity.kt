package com.topjohnwu.magisk.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.base.ActivityExtension
import com.topjohnwu.magisk.core.base.SplashController
import com.topjohnwu.magisk.core.base.SplashScreenHost
import com.topjohnwu.magisk.ui.navigation.MainNavigation
import com.topjohnwu.magisk.ui.theme.MagiskTheme
import com.topjohnwu.magisk.util.CommonActivityResultProvider
import me.edgeatzero.android.ActivityResultHandler
import me.edgeatzero.compose.util.ProvideUIMode
import me.edgeatzero.compose.util.UIMode


class MainActivity : ComponentActivity(), SplashScreenHost, CommonActivityResultProvider {

    override val GetContentHandler = ActivityResultHandler(this, ActivityResultContracts.GetContent())

    override val splashController = SplashController(this)
    override val extension = ActivityExtension(this)

    private var shouldShowInvalidStateMessage by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
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
