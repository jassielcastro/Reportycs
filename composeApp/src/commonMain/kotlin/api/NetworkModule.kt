package api

import io.ktor.client.HttpClient
import org.koin.dsl.module

expect fun provideClient(): HttpClient

val provideHttpClientModule = module {
    single { provideClient() }
}
