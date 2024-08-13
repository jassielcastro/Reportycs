package repository.di

import org.koin.dsl.module
import repository.PullRequestRepository

val repositoryModule = module {
    single { PullRequestRepository(get(), get()) }
}
