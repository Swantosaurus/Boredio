package com.swantosaurus.boredio.activity.di

import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import com.swantosaurus.boredio.activity.dataSource.ActivityDataSourceImpl
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.ImageGeneratingDataSource
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.local.GeneratedImageFileSystem
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.RemoteImageLoader
import com.swantosaurus.boredio.activity.dataSource.local.ActivityLocalDataSource
import com.swantosaurus.boredio.activity.dataSource.remote.ActivityRemoteDataSource
import com.swantosaurus.boredio.imageGenerating.ImageGenerator
import org.koin.dsl.module

val commonActivityModule = module {
    single {
        RemoteImageLoader(get())
    }
    single {
        GeneratedImageFileSystem()
    }
    single {
        ImageGenerator(get(), get())
    }
    single {
        ImageGeneratingDataSource(get(), get(), get())
    }
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
        ActivityDataSourceImpl(get(), get(), get())
    }
}