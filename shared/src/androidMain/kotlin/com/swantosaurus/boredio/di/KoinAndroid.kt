package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.SimpleCounterViewModel
import com.swantosaurus.boredio.dataSource.activity.local.AndroidDatabaseDriverFactory
import com.swantosaurus.boredio.activity.dataSource.local.DatabaseDriverFactory
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


actual val platformModule = module {
    factory<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(get())
    }
    viewModel { SimpleCounterViewModel(get()) }
}