package org.ligi.peepdroid.model

data class Peep (
        val tx: String,
        val ipfs: String,
        val author: String,
        val content: String,
        val timestamp: Int,
        val confirmed_timestamp: Int?,
        val block: Int?,
        val instant: Boolean,
        val parent: Peep,
        val avatarUrl: String
)