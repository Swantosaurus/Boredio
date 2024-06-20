package com.swantosaurus.boredio.dataSource.activity

import com.swantosaurus.boredio.dataSource.activity.model.Activity
import com.swantosaurus.boredio.dataSource.activity.remote.model.ActivityRemoteModel
import kotlinx.datetime.LocalDateTime


interface ActivityDataSource {
    suspend fun getNewRandom() : Activity

    suspend fun storeActivity(activity: Activity)

    suspend fun getActivityByKey(key: String) : Activity

    suspend fun getAllStoredActivities() : List<Activity>
}