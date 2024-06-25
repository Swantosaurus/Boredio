package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.activity.dataSource.local.DatabaseDriverFactory
import com.swantosaurus.boredio.dataSource.activity.local.AndroidDatabaseDriverFactory
import com.swantosaurus.boredio.screens.DailyFeedViewModel
import com.swantosaurus.boredio.screens.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


actual val platformModule = module {
    factory<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(context = get())
    }
    viewModel {
        SearchViewModel(activityDataSource = get(), preferences = get())
    }
    viewModel {
        DailyFeedViewModel(activityDataSource = get(), preferences = get())
    }
}