package com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.responseModel

import kotlinx.serialization.Serializable

@Serializable
data class DalleResponse (
    val created: Int,
    val data: List<DalleResponseData>
)


@Serializable
data class DalleResponseData (
    val url: String
)
