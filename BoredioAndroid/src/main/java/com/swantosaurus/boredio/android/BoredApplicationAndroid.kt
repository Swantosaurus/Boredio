package com.swantosaurus.boredio.android

import android.app.Application
import com.swantosaurus.boredio.di.NoApiKey
import com.swantosaurus.boredio.di.initKoin
import com.swantosaurus.boredio.imageGenerating.ImageGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module
import kotlin.reflect.KVisibility
import kotlin.reflect.full.staticProperties

/**
 * this property has to be set in your local properties and sould contail the OpenAI API key
 * else it wont generate any images
 */
private const val expectedProperty: String = "OPEN_AI_API_KEY"

class BoredApplicationAndroid: Application() {
    override fun onCreate() {
        super.onCreate()


        initKoin(module {
            single<ImageGenerator.OpenAiApiKey> {
                BuildConfig::class.staticProperties.forEach {
                    if (it.visibility == KVisibility.PUBLIC) {
                        if (it.name == expectedProperty) {
                            return@single object : ImageGenerator.OpenAiApiKey {
                                override val key: String
                                    get() = it.getter.call() as String
                            }
                        }
                    }
                }
                return@single NoApiKey()
            }
        }){
            androidLogger()
            androidContext(this@BoredApplicationAndroid)
        }
    }
}