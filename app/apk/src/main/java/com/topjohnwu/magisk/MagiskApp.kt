package com.topjohnwu.magisk

import android.content.Context
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.topjohnwu.magisk.core.App
import me.edgeatzero.coil.AppIconFetcher
import me.edgeatzero.coil.AppIconKeyer

class MagiskApp : App(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(AppIconFetcher.Factory(iconSize = 48.dp, shrinkNonAdaptiveIcons = false, context))
                add(AppIconKeyer())
            }
            .build()

}
