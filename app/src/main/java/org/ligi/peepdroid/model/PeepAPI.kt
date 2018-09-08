package org.ligi.peepdroid.model

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request

private const val BASE_API = "https://peepeth.com/"

class PeepAPI(private val okHttpClient: OkHttpClient,
              private val moshi: Moshi) {

    fun getPeeps() = Request.Builder().url("$BASE_API/get_peeps").build().let {
        okHttpClient.newCall(it).execute().body()?.string()
    }

    fun getNewSecret() = Request.Builder().url("$BASE_API/get_new_secret").build().let {
        moshi.adapter(SignSecret::class.java).fromJson(okHttpClient.newCall(it).execute().body()?.string())?.secret
    }

}