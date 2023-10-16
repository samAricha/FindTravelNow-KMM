package data.di

import com.russhwolf.settings.Settings
import data.repository.FlightsRepository
import data.source.preferences.UserPreferences
import data.source.preferences.UserPreferencesImpl
import data.source.remote.apiservice.FlightsApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

private val preferencesSourceModule = module {
    single { Settings() } bind Settings::class
    singleOf(::UserPreferencesImpl) bind UserPreferences::class
}

private val remoteSourceModule = module {
    single {
        HttpClient(engineFactory = CIO) {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.findtravelnow.com"
                    parameters.append("api_key", "GkRv8hS3TnWp4zYbDfVjAqXuZwE5r7t9")
                }
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json()
            }
        }
    }
    factoryOf(::FlightsApiService)
}

private val repositoryModule = module {
    factory { Dispatchers.IO } bind CoroutineContext::class
    factoryOf(::FlightsRepository)
}

val dataModule = module {
    includes(preferencesSourceModule, remoteSourceModule, repositoryModule)
}
