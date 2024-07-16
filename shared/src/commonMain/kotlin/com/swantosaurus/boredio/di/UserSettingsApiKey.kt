package com.swantosaurus.boredio.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.swantosaurus.boredio.imageGenerating.ImageGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val backgroundThread = CoroutineScope(newSingleThreadContext("UserSettingsApiKey"))

class UserSettingsApiKey(
    private val preferences: DataStore<Preferences>
) : ImageGenerator.OpenAiApiKey {

    private val apiKeyPreferencesKey = stringPreferencesKey("openai_api_key")

    var realTimeKey: Flow<String> = preferences.data.map { it[apiKeyPreferencesKey]?: "" }

    override val key: String
        get() = runBlocking { realTimeKey.firstOrNull() ?: "" }

    suspend fun setKey(to: String) {
        preferences.edit {
            it[apiKeyPreferencesKey] = to
        }
    }
}