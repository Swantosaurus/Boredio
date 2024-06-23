package com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.requestModel

import kotlinx.serialization.Serializable

@Serializable
data class Dalle3Body(
    val model: String,
    val prompt: String,
    val quality: String,
    val n : Int,
    val size: String
)

@Serializable
data class Dalle2Body(
    val model: String,
    val prompt: String,
    val n : Int,
    val size: String
)