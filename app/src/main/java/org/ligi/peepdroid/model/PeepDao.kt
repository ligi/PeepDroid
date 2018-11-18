package org.ligi.peepdroid.model

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface PeepDao {

    @get:Query("SELECT * from peeps ORDER BY timestamp DESC")
    val allPeeps: List<Peep>

    @Query("SELECT * FROM peeps ORDER BY timestamp DESC")
    fun getAllPaged(): DataSource.Factory<Int, Peep>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Peep)

    @Query("DELETE FROM peeps")
    fun deleteAll()

    @Query("SELECT * FROM peeps WHERE id = :parentPeep ")
    fun getPeepByID(parentPeep: String): Peep
}