package com.swantosaurus.boredio.image_gen

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable


private val dalle2Model = "dall-e-2"
private val dalle3Model = "dall-e-3"



@Serializable
private data class Dalle3Body(
    val model: String,
    val prompt: String,
    val quality: String,
    val n : String,
    val size: String
)

@Serializable
private data class Dalle2Body(
    val model: String,
    val prompt: String,
    val n : String,
    val size: String
)


class ImageGenerator(private val client: HttpClient) {
    val logger = Logger.withTag("ImageGenerator")
    suspend fun generate(prompt: String, model: Model, dimensions: Dimensions, quality: Quality? = null): String {
//        client.post{
//            contentType(ContentType.Application.Json)
//            headers {
//                this.append("Authorization", value = "Bearer")
//            }
//            body =
//        }.body<>()

         when(model){
            Model.Dalle2 -> {
                val body = getDalle2Body(prompt, dimensions)
                //logger.d(Json.encodeToString(body))
            }
            Model.Dalle3 -> {
                val body = getDalle3Body(prompt, dimensions, quality)
                val key =
                //logger.d(Json.encodeToString(body))

                client.post {
                    contentType(ContentType.Application.Json)
                    headers {
                        this.append("Authorization", value = "Bearer $")
                    }
                }
            }
        }


        return ""
    }

    private fun getDalle2Body(prompt: String, dimensions: Dimensions): Dalle2Body =
        Dalle2Body(
            model = dalle2Model,
            prompt = prompt,
            size = dimensions.toString(),
            n = "1"
        )


    private fun getDalle3Body(prompt: String, dimensions: Dimensions, quality: Quality? = null): Dalle3Body =
        Dalle3Body(
            model = dalle3Model,
            prompt = prompt,
            size = dimensions.toString(),
            quality = quality?.name ?: "standard",
            n = "1"
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
            "${width}x$$height"
    }
}