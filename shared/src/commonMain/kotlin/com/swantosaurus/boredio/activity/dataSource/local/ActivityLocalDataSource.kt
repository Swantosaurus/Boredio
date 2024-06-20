package com.swantosaurus.boredio.activity.dataSource.local

import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDB
import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDatabaseModel


internal class ActivityLocalDataSource(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = ActivityDB(databaseDriverFactory.createDriver())
    private val dbQuery = database.activityDBQueries

    internal fun getActivitiesByKey(key: String): ActivityDatabaseModel? {
        return dbQuery.selectActivityByKey(key).executeAsOneOrNull()
    }

    internal fun getAllActivities(): List<ActivityDatabaseModel> {
        return dbQuery.selectAllActivities().executeAsList()
    }

    internal fun getAllIgnoredActivities(): List<ActivityDatabaseModel> {
        return dbQuery.selectAllIgnoredActivities().executeAsList()
    }

    internal fun getAllCompletedActivities(): List<ActivityDatabaseModel> {
        return dbQuery.selectAllCompletedActivities().executeAsList()
    }

    internal fun getAllFavoriteActivities(): List<ActivityDatabaseModel> {
        return dbQuery.selectAllFavoriteActivities().executeAsList()
    }

    internal fun storeActivity(activity: ActivityDatabaseModel) {
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
            activity.ignore
        )
    }
}