package repository.di

import org.koin.dsl.module
import repository.local.LocalPullRequestRepository
import repository.remote.RemotePullRequestRepository
import repository.remote.RemoteUserRepository

val useCaseModule = module {
    single { RemotePullRequestRepository(get(), get()) }
    single { LocalPullRequestRepository(get()) }

    single { RemoteUserRepository(get(), get()) }
}
