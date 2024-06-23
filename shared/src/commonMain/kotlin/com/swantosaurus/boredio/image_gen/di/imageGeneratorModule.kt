package com.swantosaurus.boredio.image_gen.di

import com.swantosaurus.boredio.image_gen.ImageGenerator
import org.koin.dsl.module

val imageGeneratorModule = module {
    single {
        ImageGenerator(get())
    }
}