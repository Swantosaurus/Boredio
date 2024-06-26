package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.activity.dataSource.local.DatabaseDriverFactory
import com.swantosaurus.boredio.dataSource.activity.local.AndroidDatabaseDriverFactory
import com.swantosaurus.boredio.screenViewModels.AboutViewModel
import com.swantosaurus.boredio.screenViewModels.DailyFeedViewModel
import com.swantosaurus.boredio.screenViewModels.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


actual val platformModule = module {
    factory<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(context = get())
    }
    viewModel {
        AboutViewModel(inAppUrls = get())
    }
    viewModel {
        SearchViewModel(activityDataSource = get(), preferences = get())
    }
    viewModel {
        DailyFeedViewModel(activityDataSource = get(), preferences = get())
    }
}