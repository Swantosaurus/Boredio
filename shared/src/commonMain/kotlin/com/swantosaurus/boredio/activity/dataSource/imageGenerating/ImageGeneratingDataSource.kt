package com.swantosaurus.boredio.activity.dataSource.imageGenerating

import com.swantosaurus.boredio.activity.model.Activity

internal interface ImageGeneratingDataSource {
    suspend fun getImageForActivity(
        activity: Activity
    ): String?

    suspend fun getImageForActivity(
        activityKey: String,
        activityDescription: String
    ): String?

    suspend fun deleteImage(activity: Activity)
}