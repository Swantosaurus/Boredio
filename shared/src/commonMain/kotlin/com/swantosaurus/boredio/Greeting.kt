package com.swantosaurus.boredio

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable



class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return runBlocking {
            greeting()
        }
    }


    suspend fun greeting(): String = "test"
//        val response : Activity = client.get("https://bored.api.lewagon.com/api/activity/").body()
//        return response.toString()
//    }
}