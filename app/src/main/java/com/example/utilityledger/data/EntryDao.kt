package com.example.utilityledger.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries ORDER BY dueDate ASC")
    fun getAll(): Flow<List<LedgerEntry>>

    @Query("SELECT * FROM entries WHERE paidDate IS NULL ORDER BY dueDate ASC")
    suspend fun getAllUnpaidOnce(): List<LedgerEntry>

    @Insert
    suspend fun insert(entry: LedgerEntry): Long

    @Update
    suspend fun update(entry: LedgerEntry)

    @Delete
    suspend fun delete(entry: LedgerEntry)
}
