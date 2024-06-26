package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.imageGenerating.ImageGenerator
import com.swantosaurus.boredio.preferences.createDataStore
import com.swantosaurus.boredio.urls.InAppUrls
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

val commonModule = module {
    //http client for network requests
    single {
        ImageGenerator(client = get(), apiKey = get())
    }
    single {
        createDataStore()
    }
    single {
        InAppUrls(
            boredApi = "https://bored.api.lewagon.com/",
            dalleApi = "https://platform.openai.com/docs/guides/images/example-dall-e-3-generations",
            googleCould = "https://cloud.google.com/"
        )
    }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}