package org.ligi.peepdroid

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.chibatching.kotpref.Kotpref
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.get
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module
import org.ligi.peepdroid.api.PeepAPI
import org.ligi.peepdroid.model.SessionStore
import org.ligi.peepdroid.model.Settings
import org.ligi.peepdroid.model.SharedPrefsSettings

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
        single { SessionStore }
        single { PeepAPI(get(), get(), get()) }
        single( definition = { SharedPrefsSettings(this@App) as Settings })

    }

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        startKoin(this, listOf(koinModule))
        applyNightMode(get())
    }

}

fun applyNightMode(settings: Settings) {
    @AppCompatDelegate.NightMode val nightMode = settings.getNightMode()
    AppCompatDelegate.setDefaultNightMode(nightMode)
}