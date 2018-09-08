package org.ligi.peepdroid

import android.app.Application
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module
import org.ligi.peepdroid.model.PeepAPI


class App : Application() {

    private val koinModule = module {
        single { OkHttpClient.Builder().build() }
        single { Moshi.Builder().build() }
        single { PeepAPI(get(),get()) }
    }

    override fun onCreate(){
        super.onCreate()
        startKoin(this, listOf(koinModule))
    }
}