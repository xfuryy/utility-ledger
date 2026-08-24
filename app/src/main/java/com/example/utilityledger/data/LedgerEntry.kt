package com.example.utilityledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "entries")
data class LedgerEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val subCategory: String = "",
    val name: String = "",
    val amount: Double,
    val dueDate: LocalDate,
    val note: String = "",
    val paidDate: LocalDate? = null,
    val recurEvery: Int? = null,
    val recurUnit: RecurUnit? = null,
    val minBalance: Double? = null,
    val historyCount: Int = 0
) {
    val isRecurring: Boolean get() = recurEvery != null && recurUnit != null
    val isPaid: Boolean get() = paidDate != null
}
