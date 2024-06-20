package com.swantosaurus.boredio.dataSource.activity.local

import com.swantosaurus.boredio.dataSource.activity.local.model.ActivityLocalModel
import com.swantosaurus.boredio.dataSource.activity.model.ActivityType

class ActivityLocalDataSource {
    fun getByKey(key: String): ActivityLocalModel {
        return ActivityLocalModel(
            activity = "activity",
            type = ActivityType.MUSIC,
            participants = 1,
            price = 0.0,
            link = "link",
            key = "key",
            accessibility = 0.0,
            favorite = false,
            userRating = 0,
            fetchDate = 0,
            completed = false,
            completeDate = 0
        )
    }

    fun storeActivity(activity: ActivityLocalModel) {

    }

    fun getAllStoredActivities(): List<ActivityLocalModel> {
        return listOf(ActivityLocalModel(
            activity = "activity",
            type = ActivityType.MUSIC,
            participants = 1,
            price = 0.0,
            link = "link",
            key = "key",
            accessibility = 0.0,
            favorite = false,
            userRating = 0,
            fetchDate = 0,
            completed = false,
            completeDate = 0
        ))
    }

    fun getAllFavoriteActivities(): List<ActivityLocalModel> {
        return listOf(ActivityLocalModel(
            activity = "activity",
            type = ActivityType.MUSIC,
            participants = 1,
            price = 0.0,
            link = "link",
            key = "key",
            accessibility = 0.0,
            favorite = false,
            userRating = 0,
            fetchDate = 0,
            completed = false,
            completeDate = 0
        ))
    }

    fun getAllCompletedActivities(): List<ActivityLocalModel> {
        return listOf(ActivityLocalModel(
            activity = "activity",
            type = ActivityType.MUSIC,
            participants = 1,
            price = 0.0,
            link = "link",
            key = "key",
            accessibility = 0.0,
            favorite = false,
            userRating = 0,
            fetchDate = 0,
            completed = false,
            completeDate = 0
        ))
    }
}