package com.topjohnwu.magisk.di

import com.topjohnwu.magisk.ui.action.ActionParams
import com.topjohnwu.magisk.ui.action.ActionViewModel
import com.topjohnwu.magisk.ui.flash.FlashAction
import com.topjohnwu.magisk.ui.flash.FlashViewModel
import com.topjohnwu.magisk.ui.home.HomeViewModel
import com.topjohnwu.magisk.ui.install.InstallViewModel
import com.topjohnwu.magisk.ui.module.ModuleViewModel
import com.topjohnwu.magisk.ui.settings.SettingsViewModel
import com.topjohnwu.magisk.ui.superuser.SuperUserViewModel
import org.kodein.di.*

private const val TAG = "ViewModelModule"

val ViewModelModule = DI.Module(TAG) {
    bind<ActionViewModel>() with factory { params: ActionParams -> ActionViewModel(params, instance()) }
    bind<FlashViewModel>() with factory { action: FlashAction -> FlashViewModel(action, instance()) }
    bind<HomeViewModel>() with singleton { HomeViewModel(instance()) }
    bind<InstallViewModel>() with factory { InstallViewModel(instance()) }
    bind<ModuleViewModel>() with singleton { ModuleViewModel(instance()) }
    bind<SettingsViewModel>() with singleton { SettingsViewModel(instance()) }
    bind<SuperUserViewModel>() with singleton { SuperUserViewModel(instance()) }
}
