package org.ligi.peepdroid.model

import com.squareup.moshi.Moshi
import okhttp3.*
import org.ligi.peepdroid.SessionStore

private const val BASE_API = "https://peepeth.com"
val address = "0x0402c3407dcBD476C3d2Bbd80d1b375144bAF4a2".toLowerCase()

class PeepAPI(private val okHttpClient: OkHttpClient,
              private val moshi: Moshi,
              private val sessionStore: SessionStore) {

    fun getPeeps() = Request.Builder().url("$BASE_API/get_peeps").build().let {
        okHttpClient.newCall(it).execute().body()?.string()
    }


    fun init() = Request.Builder().url("$BASE_API/_")
            .build().let {
                okHttpClient.newCall(it).execute().body()?.string()
            }

    fun setIsUser(csrf: String) = defaultRequest()
            .header("X-CSRF-Token", csrf)
            .url("$BASE_API/set_is_user").put(RequestBody.create(null, byteArrayOf())).build().let {
                sessionStore.setCSRF(csrf)
                okHttpClient.newCall(it).execute().body()?.string()
            }

    fun getNewSecret() = defaultRequest().url("$BASE_API/get_new_secret").build().let {
        moshi.adapter(SignSecret::class.java).fromJson(okHttpClient.newCall(it).execute().body()?.string())?.secret
    }


    fun getUser() = defaultRequest().url("$BASE_API/get_account?you=true&address=$address").build().let {
        okHttpClient.newCall(it).execute().body()?.string()
    }

    fun peep(message: String): Response {

        val time = System.currentTimeMillis() / 1000

        val requestBody = FormBody.Builder()

                .add("peep[ipfs]", "xxx")
                .add("peep[author]", address)
                .add("peep[content]", message)
                .add("peep[parentID]", "")
                .add("peep[shareID]", "")
                .add("peep[twitter_share]", "false")
                .add("peep[picIpfs]", "")

                .add("peep[origContents]", """{"type":"peep","content":"$message","pic":"","untrustedAddress":"$address","untrustedTimestamp":"$time","shareID":"","parentID":""}""")
                .add("share_now", "true")
                .build()

        return defaultRequest().url("$BASE_API/create_peep")
                .header("X-CSRF-Token", sessionStore.getCSRF()).post(requestBody).build().let {
                    okHttpClient.newCall(it).execute()
                }
    }

    private fun defaultRequest() = Request.Builder()
            .header("X-Requested-With", "XMLHttpRequest")
            .addHeader("User-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:62.0) Gecko/20100101 Firefox/62.0")

}