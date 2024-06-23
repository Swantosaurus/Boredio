package com.swantosaurus.boredio.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.mp.KoinPlatformTools


actual fun createDataStore(): DataStore<Preferences> {
    val koin = KoinPlatformTools.defaultContext().get()
    val ctx : Context = koin.get()

    return PreferenceDataStoreFactory.createWithPath(produceFile = {
        ctx.filesDir.resolve(preferencesPath).absolutePath.toPath()
    })
}