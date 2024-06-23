package com.swantosaurus.boredio.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.posix.err
import platform.posix.`false`

actual fun createDataStore(): DataStore<Preferences> {
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory : NSURL? = NSFileManager.defaultManager().URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            (requireNotNull(documentDirectory).path() + "/$preferencesPath").toPath()
        }
    )
}