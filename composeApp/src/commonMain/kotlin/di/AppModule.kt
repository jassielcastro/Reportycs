package di

import api.provideHttpClientModule
import cache.di.dataBaseModule
import crypt.di.cryptoModule
import org.koin.core.module.Module
import repository.di.repositoryModule
import ui.di.viewModelModule
import usecase.di.useCaseModule

val appModule: List<Module>
    get() = listOf(
        provideHttpClientModule,
        useCaseModule,
        dataBaseModule,
        repositoryModule,
        viewModelModule,
        cryptoModule
    )
