package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.AppInfo
import com.swantosaurus.boredio.activity.dataSource.local.DatabaseDriverFactory
import com.swantosaurus.boredio.dataSource.activity.local.IOSDatabaseDriverFactory
import com.swantosaurus.boredio.fileSystem.getBaseDataPath
import com.swantosaurus.boredio.imageGenerating.ImageGenerator
import com.swantosaurus.boredio.screens.DailyFeedViewModel
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.module

actual val platformModule = module {
    factory<DatabaseDriverFactory> {
        IOSDatabaseDriverFactory()
    }
    single { DailyFeedViewModel(activityDataSource = get(), preferences = get()) }
}

fun initKoinIos(
    appInfo: AppInfo,
    doOnStartup: () -> Unit,
    openAiApiKey: ImageGenerator.OpenAiApiKey
): KoinApplication = initKoin(
    module {
        single<ImageGenerator.OpenAiApiKey> { openAiApiKey }
        single { appInfo }
        single { doOnStartup }
    }
){
    println("base dataPath: getBaseDataPath(): ${getBaseDataPath()}")
}

object KotlinDependencies : KoinComponent {
    fun getDailyFeedViewModel() = getKoin().get<DailyFeedViewModel>()
}
