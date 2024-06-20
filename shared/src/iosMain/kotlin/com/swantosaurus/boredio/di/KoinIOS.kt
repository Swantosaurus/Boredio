package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.AppInfo
import com.swantosaurus.boredio.SimpleCounterViewModel
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule = module {
    single { SimpleCounterViewModel() }
}

fun initKoinIos(
    userDefaults: NSUserDefaults,
    appInfo: AppInfo,
    doOnStartup: () -> Unit
): KoinApplication = initKoin(
    module {
        //single<Settings> { NSUserDefaultsSettings(userDefaults) }
        single { appInfo }
        single { doOnStartup }
    }
){}

object KotlinDependencies : KoinComponent {
    fun getSimpleCounterViewModel() = getKoin().get<SimpleCounterViewModel>()
}