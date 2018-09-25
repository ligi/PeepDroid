package org.ligi.peepdroid.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import org.ligi.peepdroid.model.Peeper
import java.util.*

private val moshi by lazy {  Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).build() }
private val adapter= moshi.adapter(Peeper::class.java)

fun parsePeeper(json: String) = adapter.fromJson(json)