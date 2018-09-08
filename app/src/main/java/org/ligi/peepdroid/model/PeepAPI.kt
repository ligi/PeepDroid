package org.ligi.peepdroid.model

import com.squareup.moshi.Moshi
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

private const val BASE_API = "https://peepeth.com"
val address = "0x0402c3407dcBD476C3d2Bbd80d1b375144bAF4a2".toLowerCase()

class PeepAPI(private val okHttpClient: OkHttpClient,
              private val moshi: Moshi) {

    var localCsrf: String? = null

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
                localCsrf = csrf
                okHttpClient.newCall(it).execute().body()?.string()
            }

    fun getNewSecret() = defaultRequest().url("$BASE_API/get_new_secret").build().let {
        moshi.adapter(SignSecret::class.java).fromJson(okHttpClient.newCall(it).execute().body()?.string())?.secret
    }


    fun getUser() = defaultRequest().url("$BASE_API/get_account?you=true&address=$address").build().let {
        okHttpClient.newCall(it).execute().body()?.string()
    }

    fun peep(message: String): String? {

        val content = "test"
        val requestBody = FormBody.Builder()

                .add("peep[ipfs]", "xxx")
                .add("peep[author]", address)
                .add("peep[content]", content)
                .add("peep[parentID]", "")
                .add("peep[shareID]", "")
                .add("peep[twitter_share]", "false")
                .add("peep[picIpfs]", "")
                .add("peep[origContents]", """{"type":"peep","content":"$content","pic":"","untrustedAddress":"$address","untrustedTimestamp":1536453422,"shareID":"","parentID":""}""")
                .add("share_now", "true")
                .build()

        return defaultRequest().url("$BASE_API/create_peep")
                .header("X-CSRF-Token", localCsrf).post(requestBody).build().let {
            okHttpClient.newCall(it).execute().body()?.string()
        }
    }

    fun defaultRequest() = Request.Builder()
            .header("X-Requested-With", "XMLHttpRequest")
            .addHeader("User-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:62.0) Gecko/20100101 Firefox/62.0")

}