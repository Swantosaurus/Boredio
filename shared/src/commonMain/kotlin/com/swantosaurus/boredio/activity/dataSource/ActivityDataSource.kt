package com.swantosaurus.boredio.activity.dataSource

import com.swantosaurus.boredio.activity.model.Activity


interface ActivityDataSource {
    suspend fun getDailyFeed(onImageReady: (Activity) -> Unit): List<Activity>?

    suspend fun getNewRandom(isDailyFeed: Boolean = true, onImageReady: (Activity) -> Unit) : Activity?

    suspend fun storeActivity(activity: Activity): Activity

    suspend fun getActivityByKey(key: String) : Activity?

    suspend fun getAllStoredActivities() : List<Activity>
}