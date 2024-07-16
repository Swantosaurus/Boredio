package com.swantosaurus.boredio.activity.dataSource.local

import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDB
import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDatabaseModel
import com.swantosaurus.boredio.di.DatabaseDriverFactory


internal class ActivityLocalDataSourceImpl(databaseDriverFactory: DatabaseDriverFactory)
    :ActivityLocalDataSource {
    private val database = ActivityDB(databaseDriverFactory.createDriver())
    private val dbQuery = database.activityDBQueries

    override fun getActivitiesByKey(key: String): ActivityDatabaseModel? =
        dbQuery.selectActivityByKey(key).executeAsOneOrNull()


    override fun getAllActivities(): List<ActivityDatabaseModel> =
         dbQuery.selectAllActivities().executeAsList()


    override fun getAllIgnoredActivities(): List<ActivityDatabaseModel> =
        dbQuery.selectAllIgnoredActivities().executeAsList()


    override fun getAllCompletedActivities(): List<ActivityDatabaseModel> =
        dbQuery.selectAllCompletedActivities().executeAsList()


    override fun getAllFavoriteActivities(): List<ActivityDatabaseModel> =
        dbQuery.selectAllFavoriteActivities().executeAsList()


    override fun storeActivity(activity: ActivityDatabaseModel) {
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

    override fun getDailyFeed() : List<ActivityDatabaseModel> =
        dbQuery.getDailyFeed().executeAsList()

    //idk tried to make it in query it didn't work
    override fun getAllWithGeneratedImage(): List<ActivityDatabaseModel> {
        val all = getAllActivities()
        return all.filter { it.path != null }
    }

    override fun deleteActivityByKey(key: String) {
        dbQuery.deleteActivityByKey(key)
    }

    override fun deleteActivity(activity: ActivityDatabaseModel) {
        dbQuery.deleteActivityByKey(activity.key)
    }

    override fun getAllCompletedActivitiesInTimeRange(start: Long, end: Long) =
        dbQuery.getAllCompletedActivitiesInTimeRange(start, end).executeAsList()
}