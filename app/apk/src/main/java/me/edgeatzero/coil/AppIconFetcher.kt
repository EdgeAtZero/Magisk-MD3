package me.edgeatzero.coil

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import com.topjohnwu.magisk.core.AppContext
import me.zhanghai.android.appiconloader.AppIconLoader
import kotlin.math.roundToInt


class AppIconFetcher(
    private val options: Options,
    private val loader: AppIconLoader,
    private val applicationInfo: ApplicationInfo
) : Fetcher {

    override suspend fun fetch(): FetchResult =
        ImageFetchResult(
            image = loader.loadIcon(applicationInfo).toDrawable(options.context.resources).asImage(),
            true,
            dataSource = DataSource.DISK
        )

    class Factory(
        iconSize: Dp,
        shrinkNonAdaptiveIcons: Boolean,
        context: Context
    ) : Fetcher.Factory<PackageInfo> {

        val loader = AppIconLoader(
            (iconSize.value * context.resources.displayMetrics.density).roundToInt(),
            shrinkNonAdaptiveIcons,
            context
        )

        override fun create(
            data: PackageInfo,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher = AppIconFetcher(
            options = options,
            loader = loader,
            applicationInfo = data.applicationInfo!!
        )

    }

}
