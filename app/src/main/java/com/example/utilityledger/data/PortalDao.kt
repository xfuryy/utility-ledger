package com.example.utilityledger.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PortalDao {
    @Query("SELECT * FROM portals ORDER BY dueDate ASC")
    fun getAll(): Flow<List<Portal>>

    @Query("SELECT * FROM portals WHERE paidDate IS NULL ORDER BY dueDate ASC")
    suspend fun getAllUnpaidOnce(): List<Portal>

    @Insert
    suspend fun insert(portal: Portal): Long

    @Update
    suspend fun update(portal: Portal)

    @Delete
    suspend fun delete(portal: Portal)
}
