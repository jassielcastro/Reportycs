package repository.di

import org.koin.dsl.module
import repository.local.LocalPullRequestRepository
import repository.local.LocalTokenRepository
import repository.remote.RemotePullRequestRepository
import repository.remote.RemoteUserRepository

val repositoryModule = module {
    single { RemotePullRequestRepository(get(), get(), get()) }
    single { LocalPullRequestRepository(get()) }

    single { RemoteUserRepository(get(), get()) }
    single { LocalTokenRepository(get(), get()) }
}
