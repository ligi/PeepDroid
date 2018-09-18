package org.ligi.peepdroid.model

import com.squareup.moshi.Moshi
import okhttp3.*
import org.ligi.peepdroid.SessionStore
import java.io.IOException

private const val BASE_API = "https://peepeth.com"

class PeepAPI(private val okHttpClient: OkHttpClient,
              private val moshi: Moshi,
              private val sessionStore: SessionStore) {

    fun getPeeps() = getRequest(("$BASE_API/get_peeps?oldest=0" + (sessionStore.getAddress()?.let { "&you=0xit" } ?: "")))


    fun init() = getRequest("$BASE_API/_")

    private fun getRequest(s: String) = try {
        Request.Builder().url(s)
                .build().let {
                    okHttpClient.newCall(it).execute().body()?.string()
                }
    } catch (ioe: IOException) {
        ioe.printStackTrace()
        null
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


    fun getUser() = defaultRequest().url("$BASE_API/get_account?you=true&address=${sessionStore.getAddress()}").build().let {
        okHttpClient.newCall(it).execute().body()?.string()
    }

    fun reply(message: String, parent: Peep) = peep(message, parentID = parent.ipfs)
    fun share(message: String, parent: Peep) = peep(message, shareId = parent.ipfs)

    fun peep(message: String, parentID: String = "", shareId: String = ""): Response {

        val time = System.currentTimeMillis() / 1000

        val requestBody = FormBody.Builder()

                .add("peep[ipfs]", "xxx")
                .add("peep[author]", sessionStore.getAddress())
                .add("peep[content]", message)
                .add("peep[parentID]", parentID)
                .add("peep[shareID]", shareId)
                .add("peep[twitter_share]", "false")
                .add("peep[picIpfs]", "")

                .add("peep[origContents]", """{"type":"peep","content":"$message","pic":"","untrustedAddress":"${sessionStore.getAddress()}","untrustedTimestamp":"$time","shareID":"$shareId","parentID":"$parentID"}""")
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