package com.swantosaurus.boredio.android

import android.app.Application
import android.util.Log
import com.swantosaurus.boredio.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class BoredApplicationAndroid: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("BoredApplicationAndroid", "onCreate")
        initKoin(module { }){
            androidLogger()
            androidContext(this@BoredApplicationAndroid)
        }
    }
}