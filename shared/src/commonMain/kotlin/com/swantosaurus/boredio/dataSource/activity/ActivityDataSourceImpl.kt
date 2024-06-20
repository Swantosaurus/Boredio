package com.swantosaurus.boredio.dataSource.activity

import com.swantosaurus.boredio.dataSource.activity.local.ActivityLocalDataSource
import com.swantosaurus.boredio.dataSource.activity.model.Activity
import com.swantosaurus.boredio.dataSource.activity.remote.ActivityRemoteDataSource


class ActivityDataSourceImpl(
    private val activityRemoteDataSource: ActivityRemoteDataSource,
    private val activityLocalDataSource: ActivityLocalDataSource
) : ActivityDataSource {
    override suspend fun getNewRandom(): Activity {
        val newActivity = activityRemoteDataSource.getNewRandom()
        if(activityLocalDataSource.getByKey(newActivity.key) != null) {
            //TODO log that activity already exists
            getNewRandom()
        }

        activityLocalDataSource.storeActivity(
            newActivity.toLocalModel()
        )

        return newActivity.toActivity()
    }

    override suspend fun storeActivity(activity: Activity) {
        activityLocalDataSource.storeActivity(
            activity.toLocalModel()
        )
    }



    override suspend fun getActivityByKey(key: String) : Activity {
        TODO("Not yet implemented")
    }
    override suspend fun getAllStoredActivities() : List<Activity> {
        TODO("Not yet implemented")
    }
}
