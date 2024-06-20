package com.swantosaurus.boredio.dataSource.activity.local

import app.cash.sqldelight.db.SqlDriver

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
