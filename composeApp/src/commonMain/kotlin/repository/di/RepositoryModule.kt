package repository.di

import org.koin.dsl.module
import repository.PullRequestRepository
import repository.UserRepository

val repositoryModule = module {
    single { PullRequestRepository(get(), get()) }
    single { UserRepository(get()) }
}
