package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.imageGenerating.ImageGenerator
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
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}