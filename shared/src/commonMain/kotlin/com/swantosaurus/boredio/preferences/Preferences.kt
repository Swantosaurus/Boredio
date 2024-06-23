package com.swantosaurus.boredio.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences


expect fun createDataStore(): DataStore<Preferences>

internal const val preferencesPath = "boredio.preferences_pb"