package com.topjohnwu.magisk.di

import com.topjohnwu.magisk.ui.action.ActionParams
import com.topjohnwu.magisk.ui.action.ActionViewModel
import com.topjohnwu.magisk.ui.flash.FlashParams
import com.topjohnwu.magisk.ui.flash.FlashViewModel
import com.topjohnwu.magisk.ui.home.HomeViewModel
import com.topjohnwu.magisk.ui.install.InstallViewModel
import com.topjohnwu.magisk.ui.module.ModuleViewModel
import com.topjohnwu.magisk.ui.settings.SettingsViewModel
import com.topjohnwu.magisk.ui.superuser.SuperUserViewModel
import com.topjohnwu.magisk.ui.surequest.SuRequestViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

private const val TAG = "ViewModelModule"

val ViewModelModule = DI.Module(TAG) {
    bind<ActionViewModel>() with factory { params: ActionParams -> ActionViewModel(params, instance()) }
    bind<FlashViewModel>() with factory { action: FlashParams -> FlashViewModel(action, instance()) }
    bind<HomeViewModel>() with factory { HomeViewModel(instance()) }
    bind<InstallViewModel>() with factory { InstallViewModel(instance()) }
    bind<ModuleViewModel>() with factory { ModuleViewModel(instance()) }
    bind<SettingsViewModel>() with factory { SettingsViewModel(instance()) }
    bind<SuperUserViewModel>() with factory { SuperUserViewModel(instance()) }
    bind<SuRequestViewModel>() with factory { SuRequestViewModel(instance()) }
}
