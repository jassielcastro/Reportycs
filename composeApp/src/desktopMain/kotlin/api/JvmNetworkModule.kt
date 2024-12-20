package api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun provideClient(): HttpClient = HttpClient(CIO) {
    install(HttpTimeout) {
        socketTimeoutMillis = 60_000
        requestTimeoutMillis = 60_000
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.BODY
        logger = object : Logger {
            override fun log(message: String) {
                println(message)
            }
        }
    }

    defaultRequest {
        accept(ContentType.parse("application/json; charset=utf-8"))
        contentType(ContentType.parse("application/json; charset=utf-8"))
        url("https://api.github.com/graphql")
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}
