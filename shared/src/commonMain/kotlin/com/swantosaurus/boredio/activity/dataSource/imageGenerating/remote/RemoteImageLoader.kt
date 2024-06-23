package com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes

class RemoteImageLoader(
    private val client: HttpClient
) {
    private val logger = Logger.withTag("ImageLoader")
    suspend fun loadImage(url: String): ByteArray? {
        val getImage = client.get(url)

        if (getImage.status.value !in 200..299) {
            logger.e("error loading image from given url status: ${getImage.status.value} responseBody: ${getImage.body<String>()}")
            return null
        } else {
            return getImage.readBytes()
        }
    }
}