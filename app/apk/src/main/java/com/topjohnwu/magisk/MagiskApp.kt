package com.topjohnwu.magisk

import android.content.Context
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.topjohnwu.magisk.core.App
import com.topjohnwu.magisk.di.ServiceModule
import com.topjohnwu.magisk.di.ViewModelModule
import me.edgeatzero.coil.AppIconFetcher
import me.edgeatzero.coil.AppIconKeyer
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bind
import org.kodein.di.singleton

class MagiskApp : App(), DIAware, SingletonImageLoader.Factory {

    override val di by DI.lazy {
        import(androidXModule(this@MagiskApp))
        import(ServiceModule)
        import(ViewModelModule)
    }

    override fun newImageLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(AppIconFetcher.Factory(iconSize = 48.dp, shrinkNonAdaptiveIcons = false, context))
                add(AppIconKeyer())
            }
            .build()

}
