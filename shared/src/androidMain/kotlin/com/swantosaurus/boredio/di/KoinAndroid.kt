package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.activity.dataSource.local.DatabaseDriverFactory
import com.swantosaurus.boredio.dataSource.activity.local.AndroidDatabaseDriverFactory
import com.swantosaurus.boredio.preferences.createDataStore
import com.swantosaurus.boredio.screens.DailyFeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


actual val platformModule = module {
    factory<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(get())
    }
    single {
        createDataStore()
    }
    viewModel {
        DailyFeedViewModel(get(), get(), get())
    }
}