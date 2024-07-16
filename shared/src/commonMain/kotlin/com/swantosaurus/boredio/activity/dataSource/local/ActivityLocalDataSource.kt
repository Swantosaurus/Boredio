package com.swantosaurus.boredio.activity.dataSource.local

import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDatabaseModel

internal interface ActivityLocalDataSource {
    fun getActivitiesByKey(key: String): ActivityDatabaseModel?

    fun getAllActivities(): List<ActivityDatabaseModel>

    fun getAllIgnoredActivities(): List<ActivityDatabaseModel>

    fun getAllCompletedActivities(): List<ActivityDatabaseModel>

    fun getAllFavoriteActivities(): List<ActivityDatabaseModel>

    fun storeActivity(activity: ActivityDatabaseModel)

    fun getDailyFeed(): List<ActivityDatabaseModel>

    fun getAllWithGeneratedImage(): List<ActivityDatabaseModel>

    fun deleteActivityByKey(key: String)

    fun deleteActivity(activity: ActivityDatabaseModel)

    fun getAllCompletedActivitiesInTimeRange(start: Long, end: Long): List<ActivityDatabaseModel>
}