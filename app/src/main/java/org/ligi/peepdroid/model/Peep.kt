package org.ligi.peepdroid.model

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.SET_NULL
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import android.support.annotation.NonNull
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = "peeps", foreignKeys = [
    ForeignKey(entity = Peep::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("shareId"),
            onDelete = SET_NULL),
    ForeignKey(entity = Peep::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("parentId"),
            onDelete = SET_NULL)])

data class Peep(

        @PrimaryKey
        @NonNull
        @ColumnInfo(name = "id")
        val ipfs: String,

        val tx: String?,
        val author: String,
        val content: String,
        val timestamp: Long,
        val confirmed_timestamp: Long?,
        val block: Int?,
        val instant: Boolean?,

        val parentId: String?,
        val shareId: String?,

        val avatarUrl: String,
        val image_url: String?,
        var realName: String,
        var name: String,
        var status: String
) : Parcelable

