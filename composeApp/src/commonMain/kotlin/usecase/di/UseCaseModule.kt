package usecase.di

import org.koin.dsl.module
import usecase.PullRequestUseCase
import usecase.TokenUseCase
import usecase.UserContributionUseCase

val useCaseModule = module {
    single { PullRequestUseCase(get(), get()) }
    single { TokenUseCase(get()) }
    single { UserContributionUseCase(get()) }
}
