package com.swantosaurus.boredio.activity.dataSource.local

import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDB
import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDatabaseModel


internal class ActivityLocalDataSource(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = ActivityDB(databaseDriverFactory.createDriver())
    private val dbQuery = database.activityDBQueries

    internal fun getActivitiesByKey(key: String): ActivityDatabaseModel? =
        dbQuery.selectActivityByKey(key).executeAsOneOrNull()


    internal fun getAllActivities(): List<ActivityDatabaseModel> =
         dbQuery.selectAllActivities().executeAsList()


    internal fun getAllIgnoredActivities(): List<ActivityDatabaseModel> =
        dbQuery.selectAllIgnoredActivities().executeAsList()


    internal fun getAllCompletedActivities(): List<ActivityDatabaseModel> =
        dbQuery.selectAllCompletedActivities().executeAsList()


    internal fun getAllFavoriteActivities(): List<ActivityDatabaseModel> =
        dbQuery.selectAllFavoriteActivities().executeAsList()


    internal fun storeActivity(activity: ActivityDatabaseModel) {
        if(getActivitiesByKey(activity.key) != null) {
            dbQuery.updateActivity(
                activity = activity.activity,
                type = activity.type,
                participants = activity.participants,
                price = activity.price,
                link = activity.link,
                accessibility = activity.accessibility,
                favorite = activity.favorite,
                userRating = activity.userRating,
                fetchDate = activity.fetchDate,
                completed = activity.completed,
                completeDate = activity.completeDate,
                ignore = activity.ignore,
                isDailyFeed = activity.isDailyFeed,
                path = activity.path,
                key = activity.key,
            )
        } else {
            dbQuery.insertActivity(
                activity = activity.activity,
                type = activity.type,
                participants = activity.participants,
                price = activity.price,
                link = activity.link,
                key = activity.key,
                accessibility = activity.accessibility,
                favorite = activity.favorite,
                userRating = activity.userRating,
                fetchDate = activity.fetchDate,
                completed = activity.completed,
                completeDate = activity.completeDate,
                ignore = activity.ignore,
                isDailyFeed = activity.isDailyFeed,
                path = activity.path,
            )
        }
    }

    internal fun getDailyFeed() : List<ActivityDatabaseModel> =
        dbQuery.getDailyFeed().executeAsList()

    //idk tried to make it in query it didn't work
    internal fun getAllWithGeneratedImage(): List<ActivityDatabaseModel> {
        val all = getAllActivities()
        return all.filter { it.path != null }
    }

    internal fun deleteActivityByKey(key: String) {
        dbQuery.deleteActivityByKey(key)
    }

    internal fun deleteActivity(activity: ActivityDatabaseModel) {
        dbQuery.deleteActivityByKey(activity.key)
    }

    internal fun getAllCompletedActivitiesInTimeRange(start: Long, end: Long) =
        dbQuery.getAllCompletedActivitiesInTimeRange(start, end).executeAsList()
}