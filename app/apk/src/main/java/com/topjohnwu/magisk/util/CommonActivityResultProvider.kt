package com.topjohnwu.magisk.util

import android.net.Uri
import me.edgeatzero.android.ActivityResultHandler

@Suppress("PropertyName")
interface CommonActivityResultProvider {

    val GetContentHandler: ActivityResultHandler<String, Uri?>

}
