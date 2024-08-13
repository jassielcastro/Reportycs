package crypt.di

import crypt.CryptoHandler
import org.koin.dsl.module

val cryptoModule = module {
    single {
        CryptoHandler("jira-gh-pass-1234-key-data-01234")
    }
}
