package cache.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.ajcm.jira.cache.GithubStats

actual fun createDriver(dbName: String): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY + dbName).also { driver ->
        GithubStats.Schema.create(driver)
    }
}
