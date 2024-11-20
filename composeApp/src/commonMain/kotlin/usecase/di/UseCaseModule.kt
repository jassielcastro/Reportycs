package usecase.di

import org.koin.dsl.module
import usecase.local.LocalPullRequestUseCase
import usecase.remote.RemotePullRequestUseCase
import usecase.remote.RemoteUserUseCase

val useCaseModule = module {
    single { RemotePullRequestUseCase(get(), get()) }
    single { LocalPullRequestUseCase(get()) }

    single { RemoteUserUseCase(get(), get()) }
}
