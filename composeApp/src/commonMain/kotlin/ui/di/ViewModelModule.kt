package ui.di

import org.koin.dsl.module
import ui.dashboard.DashboardViewModel
import ui.TokenViewModel
import ui.repositories.CreateNewRepositoryViewModel
import ui.splash.SplashViewModel
import ui.statics.StaticsViewModel

val viewModelModule = module {
    factory { SplashViewModel(get()) }
    factory { CreateNewRepositoryViewModel(get()) }
    factory { DashboardViewModel(get()) }
    factory { TokenViewModel(get(), get()) }
    factory { StaticsViewModel(get()) }
}
