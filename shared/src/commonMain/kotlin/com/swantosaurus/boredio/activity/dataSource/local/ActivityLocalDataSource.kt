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
                activity.activity,
                activity.type,
                activity.participants,
                activity.price,
                activity.link,
                activity.accessibility,
                activity.favorite,
                activity.userRating,
                activity.fetchDate,
                activity.completed,
                activity.completeDate,
                activity.ignore,
                activity.isDailyFeed,
                activity.key,
            )
        } else {
            dbQuery.insertActivity(
                activity.activity,
                activity.type,
                activity.participants,
                activity.price,
                activity.link,
                activity.key,
                activity.accessibility,
                activity.favorite,
                activity.userRating,
                activity.fetchDate,
                activity.completed,
                activity.completeDate,
                activity.ignore,
                activity.isDailyFeed
            )
        }
    }

    internal fun getDailyFeed() : List<ActivityDatabaseModel> =
        dbQuery.getDailyFeed().executeAsList()
}