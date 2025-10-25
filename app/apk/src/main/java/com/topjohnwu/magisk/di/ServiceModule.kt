package com.topjohnwu.magisk.di

import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.topjohnwu.magisk.core.data.SuLogDao
import com.topjohnwu.magisk.core.data.magiskdb.PolicyDao
import com.topjohnwu.magisk.core.data.magiskdb.SettingsDao
import com.topjohnwu.magisk.core.data.magiskdb.StringDao
import com.topjohnwu.magisk.core.di.ServiceLocator
import com.topjohnwu.magisk.core.repository.LogRepository
import com.topjohnwu.magisk.core.repository.NetworkService
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

private const val TAG = "ServiceModule"

val ServiceModule = DI.Module(TAG) {
    bind<ImageLoader>() with singleton { SingletonImageLoader.get(instance()) }
    bind<Json>() with singleton { Json }

    // For ServiceLocator
    bind<PolicyDao>() with provider { ServiceLocator.policyDB }
    bind<SettingsDao>() with provider { ServiceLocator.settingsDB }
    bind<StringDao>() with provider { ServiceLocator.stringDB }
    bind<SuLogDao>() with provider { ServiceLocator.sulogDB }
    bind<LogRepository>() with provider { ServiceLocator.logRepo }
    bind<NetworkService>() with provider { ServiceLocator.networkService }
}
