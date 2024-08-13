package usecase.di

import org.koin.dsl.module
import usecase.local.LocalPullRequestUseCase
import usecase.remote.RemotePullRequestUseCase

val useCaseModule = module {
    single { RemotePullRequestUseCase(get(), get()) }
    single { LocalPullRequestUseCase(get()) }
}
