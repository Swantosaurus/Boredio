package com.swantosaurus.boredio.activity.dataSource

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.activity.dataSource.local.ActivityLocalDataSource
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.activity.dataSource.remote.ActivityRemoteDataSource


private val ATTEMPTS = 4


internal class ActivityDataSourceImpl(
    private val activityRemoteDataSource: ActivityRemoteDataSource,
    private val activityLocalDataSource: ActivityLocalDataSource
) : ActivityDataSource {
    private val logger = Logger.withTag("ActivityDataSourceImpl")

    override suspend fun getNewRandom(): Activity? = getNewRandomInternal(ATTEMPTS)

    override suspend fun storeActivity(activity: Activity) {
        try {
            activityLocalDataSource.storeActivity(
                activity.toDatabaseModel()
            )
        } catch (e: Exception) {
            logger.e(e) { "Error storing activity to Database" }
        }
    }

    override suspend fun getActivityByKey(key: String) : Activity? {
        try {
            getDbActivityByKey(key)?.let {
                return it
            }

            activityRemoteDataSource.getActivityByKey(key).let {
                storeActivity(it.toActivity())
                return it.toActivity()
            }
        } catch (e: Exception) {
            logger.e(e) { "Error fetching activity by key" }
            return null
        }
    }
    override suspend fun getAllStoredActivities() : List<Activity> {
        return activityLocalDataSource.getAllActivities().map { it.toActivity() }
    }


    /**
     * tries to get a new random activity from the remote source
     * if the activity already exists in the database it will retry [attempts] times
     */
    private suspend fun getNewRandomInternal(attempts: Int): Activity? {
        if(attempts <= 0) return null

        val newActivity = try {
            activityRemoteDataSource.getNewRandom()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.e() { "Error fetching new activity" }
            return@getNewRandomInternal null
        }.toActivity()

        getDbActivityByKey(newActivity.key)?.let {

            Logger.w("$newActivity Activity already exists in database retrying")
            getNewRandomInternal(attempts - 1)
        }

        storeActivity(newActivity)
        return newActivity
    }

    private fun getDbActivityByKey(key: String) : Activity? =
        activityLocalDataSource.getActivitiesByKey(key)?.let {
            return it.toActivity()
        }
}
