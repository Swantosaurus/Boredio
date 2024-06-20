package com.swantosaurus.boredio.dataSource.acticity.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.swantosaurus.boredio.dataSource.activity.local.DatabaseDriverFactory
import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDB

class AndroidDatabaseDriverFactory(private val context: Context): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(ActivityDB.Schema, context, "ActivityDb")
    }
}