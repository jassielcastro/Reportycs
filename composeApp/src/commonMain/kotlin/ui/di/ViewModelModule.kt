package ui.di

import org.koin.dsl.module
import ui.dashboard.DashboardViewModel
import ui.repositories.RepositoryViewModel
import ui.repositories.create.CreateNewRepositoryViewModel
import ui.splash.SplashViewModel

val viewModelModule = module {
    factory { SplashViewModel(get()) }
    factory { RepositoryViewModel(get()) }
    factory { CreateNewRepositoryViewModel(get(), get()) }
    factory { DashboardViewModel(get()) }
}
