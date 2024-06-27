package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.AppInfo
import com.swantosaurus.boredio.activity.dataSource.local.DatabaseDriverFactory
import com.swantosaurus.boredio.dataSource.activity.local.IOSDatabaseDriverFactory
import com.swantosaurus.boredio.imageGenerating.ImageGenerator
import com.swantosaurus.boredio.screenViewModels.AboutViewModel
import com.swantosaurus.boredio.screenViewModels.AccountViewModel
import com.swantosaurus.boredio.screenViewModels.DailyFeedViewModel
import com.swantosaurus.boredio.screenViewModels.SearchViewModel
import com.swantosaurus.boredio.screenViewModels.StorageViewModel
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.module

actual val platformModule = module {
    factory<DatabaseDriverFactory> {
        IOSDatabaseDriverFactory()
    }
    single { StorageViewModel(activityDataSource = get()) }
    single { AccountViewModel(dataSource = get(), generatedImageFileSystem = get()) }
    single { SearchViewModel(activityDataSource = get(), get()) }
    single { DailyFeedViewModel(activityDataSource = get(), preferences = get()) }
    single { AboutViewModel(inAppUrls = get()) }
}

fun initKoinIos(
    appInfo: AppInfo,
    doOnStartup: () -> Unit,
    openAiApiKey: ImageGenerator.OpenAiApiKey?
): KoinApplication = initKoin(
    module {
        single<ImageGenerator.OpenAiApiKey> { openAiApiKey ?: NoApiKey() }
        single { appInfo }
        single { doOnStartup }
    }
){}

object KotlinDependencies : KoinComponent {
    fun getDailyFeedViewModel() = getKoin().get<DailyFeedViewModel>()
    fun getSearchViewModel() = getKoin().get<SearchViewModel>()
    fun getAboutViewModel() = getKoin().get<AboutViewModel>()
    fun getAccountViewModel() = getKoin().get<AccountViewModel>()
    fun getStorageViewModel() = getKoin().get<StorageViewModel>()
}
