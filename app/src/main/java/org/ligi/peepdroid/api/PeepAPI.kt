package org.ligi.peepdroid.api

import com.squareup.moshi.Moshi
import okhttp3.*
import org.ligi.peepdroid.model.Peep
import org.ligi.peepdroid.model.PeepethPicture
import org.ligi.peepdroid.model.SessionStore
import org.ligi.peepdroid.model.SignSecret
import java.io.IOException

private const val BASE_API = "https://peepeth.com"

class PeepAPI(private val okHttpClient: OkHttpClient,
              private val moshi: Moshi,
              private val sessionStore: SessionStore) {

    fun getPeeps() = getRequest(("$BASE_API/get_peeps?oldest=0" + (SessionStore.address?.let { "&you=0x$it" } ?: "")))

    fun init() = getRequest("$BASE_API/_")

    fun getPeeper(address: String, you: Boolean, include_following: Boolean) = getRequest(("$BASE_API/get_account?address=0x$address&you=$you&include_following=$include_following"))

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
                SessionStore.csrf = csrf
                okHttpClient.newCall(it).execute().body()?.string()
            }

    fun getNewSecret() = defaultRequest().url("$BASE_API/get_new_secret").build().let {
        moshi.adapter(SignSecret::class.java).fromJson(okHttpClient.newCall(it).execute().body()?.string())?.secret
    }


    fun getUser() = defaultRequest().url("$BASE_API/get_account?you=true&address=${SessionStore.address}").build().let {
        okHttpClient.newCall(it).execute().body()?.string()
    }

    fun reply(message: String, parent: Peep, image: PeepethPicture?) = peep(message, parentID = parent.ipfs, shareId = "", picture = image)
    fun share(message: String, parent: Peep) = peep(message, shareId = parent.ipfs)

    fun peep(message: String,
             parentID: String = "",
             shareId: String = "",
             picture: PeepethPicture? = null): Response {

        val time = System.currentTimeMillis() / 1000

        val requestBody = FormBody.Builder()

                .add("peep[ipfs]", "xxx")
                .add("peep[author]", SessionStore.address)
                .add("peep[content]", message)
                .add("peep[parentID]", parentID)
                .add("peep[shareID]", shareId)
                .add("peep[twitter_share]", "false")
                .add("peep[picIpfs]", picture?.ipfsHash ?: "")
                .add("peep[image_id]", picture?.serverID ?: "")
                .add("peep[origContents]", """{"type":"peep","content":"$message","pic":"","untrustedAddress":"${SessionStore.address}","untrustedTimestamp":"$time","shareID":"$shareId","parentID":"$parentID"}""")
                .add("share_now", "true")
                .build()

        return defaultRequest().url("$BASE_API/create_peep")
                .header("X-CSRF-Token", SessionStore.csrf).post(requestBody).build().let {
                    okHttpClient.newCall(it).execute()
                }
    }

    fun love(messageID: String): Response {

        val requestBody = FormBody.Builder()
                .add("love[messageID]", messageID)
                .build()

        return defaultRequest().url("$BASE_API/loves")
                .header("X-CSRF-Token", SessionStore.csrf).post(requestBody).build().let {
                    okHttpClient.newCall(it).execute()
                }
    }


    private fun defaultRequest() = Request.Builder()
            .header("X-Requested-With", "XMLHttpRequest")
            .addHeader("User-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:62.0) Gecko/20100101 Firefox/62.0")

    fun uploadImage(foo: ByteArray): Response {

        val requestBody = MultipartBody.Builder()

                .addFormDataPart("image[peep_pic]", "test.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), foo))
                .addFormDataPart("image[address]", "0x" + SessionStore.address)
                .build()

        return defaultRequest().url("$BASE_API/images.js")
                .header("X-CSRF-Token", SessionStore.csrf).post(requestBody).build().let {
                    okHttpClient.newCall(it).execute()
                }
    }

}