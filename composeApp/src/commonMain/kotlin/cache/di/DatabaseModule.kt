package cache.di

import app.cash.sqldelight.db.SqlDriver
import cache.DataBase
import org.koin.dsl.module

expect fun createDriver(dbName: String): SqlDriver

val dataBaseModule = module {
    single { createDriver("githubRepositoryStats.db") }
    single { DataBase(get()) }
}
