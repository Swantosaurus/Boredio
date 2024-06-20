package com.swantosaurus.boredio.activity.di

import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import com.swantosaurus.boredio.activity.dataSource.ActivityDataSourceImpl
import com.swantosaurus.boredio.activity.dataSource.local.ActivityLocalDataSource
import com.swantosaurus.boredio.activity.dataSource.remote.ActivityRemoteDataSource
import org.koin.dsl.module

val commonActivityModule = module {
    single {
        ActivityLocalDataSource(get())
    }
    factory {
        ActivityLocalDataSource(get())
    }
    factory {
        ActivityRemoteDataSource(get())
    }
    factory<ActivityDataSource> {
        ActivityDataSourceImpl(get(), get())
    }
}