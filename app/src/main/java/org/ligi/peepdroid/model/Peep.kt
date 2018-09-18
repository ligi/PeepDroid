package org.ligi.peepdroid.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Peep (
        val tx: String?,
        val ipfs: String,
        val author: String,
        val content: String,
        val timestamp: Long,
        val confirmed_timestamp: Long?,
        val block: Int?,
        val instant: Boolean?,
        val parent: Peep?,
        val avatarUrl: String,
        var realName: String,
        var name: String,
        var status: String
) : Parcelable
