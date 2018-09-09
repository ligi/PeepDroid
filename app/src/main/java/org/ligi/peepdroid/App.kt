package org.ligi.peepdroid

import android.app.Application
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module
import org.ligi.peepdroid.model.PeepAPI

class App : Application() {

    private val koinModule = module {
        single {
            val sharedPrefsCookiePersistor = SharedPrefsCookiePersistor(this@App)

            val persistentCookieJar = PersistentCookieJar(SetCookieCache(), sharedPrefsCookiePersistor)

            OkHttpClient.Builder()
                    .cookieJar(persistentCookieJar)
                    .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.HEADERS
                    })
                    .build()
        }

        single { Moshi.Builder().build() }
        single { SessionStore(this@App) }
        single { PeepAPI(get(), get(), get()) }

    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(koinModule))
    }
}