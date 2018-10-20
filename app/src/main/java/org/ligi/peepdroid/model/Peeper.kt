package org.ligi.peepdroid.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class Peeper (
        val address: String?,
        val avatarUrl: String,
        val backgroundUrl: String,
        val followerCount: Int,
        val following: List<String>,

        val followingCount :Int,
        val info: String,
        val location:String,
        val memberSince: Date,
        val messageCount: Int,
        val messageToWorld:String,
        val name: String,
        val realName: String,
        val slug:String,
        val timeStamp: Int,
        val twitterHandle: String,
        val githubHandle: String,
        val tx:String,
        val website: String,
        val emailReminder: Boolean,
        val malaria_nets: Int,
        val last_streak: Int,
        val last_peep: String?,
        val nextLove: Int,
        val verification_to_show: String
) : Parcelable
