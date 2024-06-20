package com.swantosaurus.boredio.activity.dataSource

import com.swantosaurus.boredio.activity.model.Activity


interface ActivityDataSource {
    suspend fun getNewRandom() : Activity?

    suspend fun storeActivity(activity: Activity)

    suspend fun getActivityByKey(key: String) : Activity?

    suspend fun getAllStoredActivities() : List<Activity>
}