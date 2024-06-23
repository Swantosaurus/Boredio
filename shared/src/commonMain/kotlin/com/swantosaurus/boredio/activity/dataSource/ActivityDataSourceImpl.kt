package com.swantosaurus.boredio.activity.dataSource

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.ImageGeneratingDataSource
import com.swantosaurus.boredio.activity.dataSource.local.ActivityLocalDataSource
import com.swantosaurus.boredio.activity.dataSource.remote.ActivityRemoteDataSource
import com.swantosaurus.boredio.activity.model.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


private const val FETCH_ATTEMPTS = 4
private const val DAILY_FEED_CNT = 5

internal class ActivityDataSourceImpl(
    private val activityRemoteDataSource: ActivityRemoteDataSource,
    private val activityLocalDataSource: ActivityLocalDataSource,
    private val imageGeneratingDataSource: ImageGeneratingDataSource
) : ActivityDataSource {
    private val logger = Logger.withTag("ActivityDataSourceImpl")
    private val backgroundQueue = CoroutineScope(Dispatchers.IO)

    override suspend fun getDailyFeed(onImageReady: (Activity) -> Unit): List<Activity>? {
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
                getNewRandom(onImageReady = onImageReady)
            }
            return if(newFeed.any { it == null }){
                null
            } else {
                newFeed.map { it!! }
            }
        }
    }

    override suspend fun getNewRandom(isDailyFeed : Boolean, onImageReady: (Activity) -> Unit): Activity? = getNewRandomInternal(FETCH_ATTEMPTS, isDailyFeed, onImageReady)

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
                it.toActivity(false).let { activity ->
                    imageGeneratingDataSource.getImageForActivity(activity)
                    storeActivity(activity)
                    return activity
                }
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
    private suspend fun getNewRandomInternal(attempts: Int, isDailyFeed: Boolean, onImageReady: (Activity) -> Unit): Activity? {
        if(attempts <= 0) return null

        var newActivity = try {
            activityRemoteDataSource.getNewRandom()
        } catch (e: Exception) {
            logger.e(e){ "Error fetching new activity" }
            return@getNewRandomInternal null
        }.toActivity(isDailyFeed = isDailyFeed)

        getDbActivityByKey(newActivity.key)?.let {
            if(it.completed || it.ignore) {
                logger.w("$newActivity Activity already exists in database and completed")
                return getNewRandomInternal(attempts - 1, isDailyFeed, onImageReady)
            }
        }

        storeActivity(newActivity)
        //this is slow -- we run it in the background
        backgroundQueue.launch {
            val imgPath = imageGeneratingDataSource.getImageForActivity(newActivity)

            if(imgPath != null) {
                logger.i { "image path generated: $imgPath" }

                newActivity = newActivity.copy(path = imgPath)
                storeActivity(newActivity)
                onImageReady(newActivity)
            } else {
                logger.e { "Error generating image for activity" }
            }
        }


        return newActivity
    }

    private fun getDbActivity(activity: Activity): Activity = getDbActivityByKey(activity.key)!!

    private fun getDbActivityByKey(key: String) : Activity? =
        activityLocalDataSource.getActivitiesByKey(key)?.let {
            return it.toActivity()
        }
}
