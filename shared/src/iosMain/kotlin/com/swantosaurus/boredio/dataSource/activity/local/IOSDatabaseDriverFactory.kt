package com.swantosaurus.boredio.dataSource.activity.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.swantosaurus.boredio.di.DatabaseDriverFactory
import com.swantosaurus.boredio.dataSource.activity.local.db.ActivityDB

class IOSDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(ActivityDB.Schema, "ActivityDb")
    }
}