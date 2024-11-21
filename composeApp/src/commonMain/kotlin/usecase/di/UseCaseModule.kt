package usecase.di

import org.koin.dsl.module
import usecase.PullRequestUseCase
import usecase.UserContributionUseCase

val repositoryModule = module {
    single { PullRequestUseCase(get(), get()) }
    single { UserContributionUseCase(get(), get()) }
}
