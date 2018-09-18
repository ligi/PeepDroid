package org.ligi.peepdroid.model

interface Settings {

    fun getNightMode(): Int

    fun isTimeWanted(): Boolean
    fun isAvatarsWanted(): Boolean

}
