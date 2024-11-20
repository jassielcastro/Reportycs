package usecase.di

import org.koin.dsl.module
import usecase.PullRequestUseCase
import usecase.UserUseCase

val repositoryModule = module {
    single { PullRequestUseCase(get(), get()) }
    single { UserUseCase(get()) }
}
