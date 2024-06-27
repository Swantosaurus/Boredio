package com.swantosaurus.boredio.activity.dataSource

import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.activity.model.ActivityType


interface ActivityDataSource {
    suspend fun getDailyFeed(onImageReady: (Activity) -> Unit): List<Activity>?

    suspend fun getNewRandom(isDailyFeed: Boolean = true, onImageReady: (Activity) -> Unit) : Activity?

    /**
     * if generateImage is true storeLocal must be true
     */
    suspend fun getRandomByParameters(
        types: List<ActivityType> = emptyList(),
        minParticipants: Int? = null,
        maxParticipants: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minAccessibility: Double? = null,
        maxAccessibility: Double? = null,
        storeLocal: Boolean = false,
        generateImage: Boolean = false,
        onImageReady: (Activity) -> Unit = {}
    ): Activity?

    suspend fun storeActivity(activity: Activity): Activity

    suspend fun getActivityByKey(key: String) : Activity?

    suspend fun getAllStoredActivities() : List<Activity>

    suspend fun getAllCompletedActivities() : List<Activity>

    suspend fun getFavoriteActivities() : List<Activity>

    suspend fun getAllIgnoredActivities() : List<Activity>

    suspend fun getCompletedBetweenDates(startDate: Long, endDate: Long) : List<Activity>

    suspend fun getAllWithGeneratedImage() : List<Activity>

    suspend fun deleteActivity(activity: Activity)

    suspend fun deleteImage(activity: Activity)
}