package com.swantosaurus.boredio.dataSource.activity.local

import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDB
import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDatabaseModel


internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = ActivityDB(databaseDriverFactory.createDriver())
    private val dbQuery = database.activityDBQueries


    internal fun getAllActivities(): List<ActivityDatabaseModel> {
        return dbQuery.selectAllActivities().executeAsList()
    }
}