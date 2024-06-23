package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.activity.di.commonActivityModule
import com.swantosaurus.boredio.image_gen.di.imageGeneratorModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

expect val platformModule: Module


fun initKoin(module: Module, builder: KoinApplication.() -> Any): KoinApplication {
    val koinApp = startKoin {
        builder()
        modules(platformModule + module + commonModule + commonActivityModule + imageGeneratorModule)
    }

    return koinApp
}