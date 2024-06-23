package com.swantosaurus.boredio.android

import android.app.Application
import android.util.Log
import com.swantosaurus.boredio.di.initKoin
import com.swantosaurus.boredio.imageGenerating.ImageGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class BoredApplicationAndroid: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("BoredApplicationAndroid", "onCreate")
        initKoin(module {
            single<ImageGenerator.OpenAiApiKey> {
                object: ImageGenerator.OpenAiApiKey {
                    override val key: String
                        get() = BuildConfig.OPEN_AI_API_KEY
                }
            }
        }){
            androidLogger()
            androidContext(this@BoredApplicationAndroid)
        }
    }
}