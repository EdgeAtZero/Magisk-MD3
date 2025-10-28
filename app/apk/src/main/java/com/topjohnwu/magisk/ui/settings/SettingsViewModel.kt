package com.topjohnwu.magisk.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

class SettingsViewModel(app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

}
