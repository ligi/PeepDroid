package org.ligi.peepdroid.model

import com.chibatching.kotpref.KotprefModel

object SessionStore: KotprefModel() {

    var address by nullableStringPref(default = null)
    var csrf by stringPref(default = "")
}