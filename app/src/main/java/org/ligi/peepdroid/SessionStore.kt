package org.ligi.peepdroid

import android.content.Context

private const val KEY_CSRF = "csrf"
private const val KEY_ADDRESS = "address"

class SessionStore(val context: Context) {

    private val prefs by lazy {
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
    }

    fun setCSRF(csrf: String) {
        prefs.edit().putString(KEY_CSRF, csrf).apply()
    }

    fun getCSRF(): String = prefs.getString(KEY_CSRF, "")

    fun setAddress(address: String?) {
        prefs.edit().putString(KEY_ADDRESS, address).apply()
    }

    fun getAddress(): String? = prefs.getString(KEY_ADDRESS, null)

}