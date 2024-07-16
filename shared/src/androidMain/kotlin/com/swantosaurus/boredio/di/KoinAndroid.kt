package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.dataSource.activity.local.AndroidDatabaseDriverFactory
import com.swantosaurus.boredio.screenViewModels.AboutViewModel
import com.swantosaurus.boredio.screenViewModels.AccountViewModel
import com.swantosaurus.boredio.screenViewModels.DailyFeedViewModel
import com.swantosaurus.boredio.screenViewModels.SearchViewModel
import com.swantosaurus.boredio.screenViewModels.StorageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


actual val platformModule = module {
    factory<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(context = get())
    }
    viewModel {
        StorageViewModel(activityDataSource = get())
    }
    viewModel {
        AboutViewModel(inAppUrls = get())
    }
    viewModel {
        AccountViewModel(dataSource = get(), generatedImageFileSystem = get(), apiKey = get())
    }
    viewModel {
        SearchViewModel(activityDataSource = get(), preferences = get())
    }
    viewModel {
        DailyFeedViewModel(activityDataSource = get(), preferences = get())
    }
}