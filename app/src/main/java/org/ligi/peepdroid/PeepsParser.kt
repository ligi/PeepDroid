package org.ligi.peepdroid

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.ligi.peepdroid.model.Peep




fun parsePeeps(json: String): List<Peep> {
    val moshi = Moshi.Builder().build()

    val listMyData = Types.newParameterizedType(List::class.java, Peep::class.java)
    val adapter : JsonAdapter<List<Peep>> = moshi.adapter(listMyData)

    return adapter.fromJson(json)!!
}