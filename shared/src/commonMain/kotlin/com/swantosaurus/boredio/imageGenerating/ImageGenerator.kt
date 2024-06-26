package com.swantosaurus.boredio.imageGenerating

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.requestModel.Dalle2Body
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.requestModel.Dalle3Body
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.responseModel.DalleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


private const val dalle2Model = "dall-e-2"
private const val dalle3Model = "dall-e-3"

class ImageGenerator(private val client: HttpClient, private val apiKey: OpenAiApiKey) {
    private val logger = Logger.withTag("ImageGenerator")

    private val jsonDecoder = Json { ignoreUnknownKeys = true }
    suspend fun generate(prompt: String, model: Model, dimensions: Dimensions, quality: Quality? = null): String? {
        if(apiKey.key.isEmpty()){
            logger.e{ "Api Key is missing -- not generating Image" }
            return null
        }
        try {
            when (model) {
                Model.Dalle2 -> {
                    val sendBody = getDalle2Body(prompt, dimensions)

                    val dalleResponse = client.post {
                        headers {
                            append(HttpHeaders.Authorization, value = "Bearer ${apiKey.key}")
                            append(HttpHeaders.ContentType, ContentType.Application.Json)
                        }
                        setBody(sendBody)
                        url("https://api.openai.com/v1/images/generations")
                    }

                    return handleDalleResponse(dalleResponse)
                }

                Model.Dalle3 -> {
                    val sendBody = getDalle3Body(prompt, dimensions, quality)

                    val dalleResponse = client.post {
                        headers {
                            append(HttpHeaders.Authorization, value = "Bearer ${apiKey.key}")
                            append(HttpHeaders.ContentType, ContentType.Application.Json)
                        }
                        setBody(sendBody)
                        url("https://api.openai.com/v1/images/generations")
                    }

                    return handleDalleResponse(response = dalleResponse)
                }
            }
        } catch (e: Exception) {
            logger.e("Error loading Dalle image", e)
            return null
        }
    }


    private suspend fun handleDalleResponse(response: HttpResponse): String {
        val stringBody: String = response.body()

        if(response.status.value !in 200..299) {
            logger.e("status code: ${response.status.value}\n data: ${stringBody}")
            throw IllegalStateException("Error status code")
        } else {
            val responseData: DalleResponse = jsonDecoder.decodeFromString(stringBody)
            return responseData.data.first().url
        }
    }

    private fun getDalle2Body(prompt: String, dimensions: Dimensions): Dalle2Body =
        Dalle2Body(
            model = dalle2Model,
            prompt = prompt,
            size = dimensions.toString(),
            n = 1
        )


    private fun getDalle3Body(prompt: String, dimensions: Dimensions, quality: Quality? = null): Dalle3Body =
        Dalle3Body(
            model = dalle3Model,
            prompt = prompt,
            size = dimensions.toString(),
            quality = quality?.name ?: "standard",
            n = 1
        )


    enum class Model{
        Dalle2,
        Dalle3;
    }

    enum class Quality {
        hd,
        standard
    }

    @Serializable
    data class Dimensions(
        val width: Int,
        val height: Int
    ){
        override fun toString(): String =
            "${width}x$height"

        companion object {
            val XY256 = Dimensions(256, 256)
            val XY512 = Dimensions(512, 512)
            val XY1024 = Dimensions(1024, 1024)
        }
    }

    interface OpenAiApiKey {
        val key: String
    }
}