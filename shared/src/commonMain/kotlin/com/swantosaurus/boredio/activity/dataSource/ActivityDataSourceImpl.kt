package com.swantosaurus.boredio.activity.dataSource

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.activity.dataSource.local.ActivityLocalDataSource
import com.swantosaurus.boredio.activity.dataSource.remote.ActivityRemoteDataSource
import com.swantosaurus.boredio.activity.model.Activity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


private const val FETCH_ATTEMPTS = 4
private const val DAILY_FEED_CNT = 10

internal class ActivityDataSourceImpl(
    private val activityRemoteDataSource: ActivityRemoteDataSource,
    private val activityLocalDataSource: ActivityLocalDataSource
) : ActivityDataSource {
    private val logger = Logger.withTag("ActivityDataSourceImpl")

    override suspend fun getDailyFeed(): List<Activity>? {
        var dailyFeedDb = activityLocalDataSource.getDailyFeed().map { it.toActivity() }
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        if(dailyFeedDb.firstOrNull()?.fetchDate?.date != today) {
            dailyFeedDb.forEach {
                activityLocalDataSource.storeActivity(it.copy(isDailyFeed = false).toDatabaseModel())
            }
            dailyFeedDb = emptyList()
        }

        if(dailyFeedDb.isNotEmpty()) {
            return dailyFeedDb
        } else {
            val newFeed = List(DAILY_FEED_CNT) {
                getNewRandom()
            }
            return if(newFeed.any { it == null }){
                null
            } else {
                newFeed.map { it!! }
            }
        }
    }

    override suspend fun getNewRandom(isDailyFeed : Boolean): Activity? = getNewRandomInternal(FETCH_ATTEMPTS, isDailyFeed)

    override suspend fun storeActivity(activity: Activity): Activity {
        try {
            activityLocalDataSource.storeActivity(
                activity.toDatabaseModel()
            )
            return getDbActivity(activity)
        } catch (e: Exception) {
            logger.e(e) { "Error storing activity to Database" }
            return getDbActivity(activity)
        }
    }

    override suspend fun getActivityByKey(key: String) : Activity? {
        try {
            getDbActivityByKey(key)?.let {
                return it
            }

            activityRemoteDataSource.getActivityByKey(key).let {
                storeActivity(it.toActivity(false))
                return it.toActivity(false)
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
    private suspend fun getNewRandomInternal(attempts: Int, isDailyFeed: Boolean): Activity? {
        if(attempts <= 0) return null

        val newActivity = try {
            activityRemoteDataSource.getNewRandom()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.e { "Error fetching new activity" }
            return@getNewRandomInternal null
        }.toActivity(isDailyFeed = isDailyFeed)

        getDbActivityByKey(newActivity.key)?.let {
            if(it.completed || it.ignore) {
                logger.w("$newActivity Activity already exists in database and completed")
                getNewRandomInternal(attempts - 1, isDailyFeed)
            }
        }

        storeActivity(newActivity)
        return newActivity
    }

    private fun getDbActivity(activity: Activity): Activity = getDbActivityByKey(activity.key)!!

    private fun getDbActivityByKey(key: String) : Activity? =
        activityLocalDataSource.getActivitiesByKey(key)?.let {
            return it.toActivity()
        }
}
