package com.swantosaurus.boredio.activity.di

import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import com.swantosaurus.boredio.activity.dataSource.ActivityDataSourceImpl
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.ImageGeneratingDataSource
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.ImageGeneratingDataSourceImpl
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.local.GeneratedImageFileSystem
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.RemoteImageLoader
import com.swantosaurus.boredio.activity.dataSource.local.ActivityLocalDataSource
import com.swantosaurus.boredio.activity.dataSource.local.ActivityLocalDataSourceImpl
import com.swantosaurus.boredio.activity.dataSource.remote.ActivityRemoteDataSource
import com.swantosaurus.boredio.activity.dataSource.remote.ActivityRemoteDataSourceImpl
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
    single<ImageGeneratingDataSource> {
        ImageGeneratingDataSourceImpl(get(), get(), get())
    }
    factory<ActivityLocalDataSource> {
        ActivityLocalDataSourceImpl(get())
    }
    factory<ActivityRemoteDataSource> {
        ActivityRemoteDataSourceImpl(get())
    }
    factory<ActivityDataSource> {
        ActivityDataSourceImpl(get(), get(), get())
    }
}