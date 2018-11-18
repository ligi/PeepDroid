
package org.ligi.peepdroid.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(Peep::class), version = 1)
abstract class PeepDatabase : RoomDatabase() {
    abstract fun peepDao(): PeepDao

}