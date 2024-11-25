package di

import api.provideHttpClientModule
import cache.di.dataBaseModule
import crypt.di.cryptoModule
import org.koin.core.module.Module
import usecase.di.useCaseModule
import ui.di.viewModelModule
import repository.di.repositoryModule

val appModule: List<Module>
    get() = listOf(
        provideHttpClientModule,
        repositoryModule,
        dataBaseModule,
        useCaseModule,
        viewModelModule,
        cryptoModule
    )
