package com.example.utilityledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "portals")
data class Portal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val minBalance: Double,
    val currentBalance: Double,
    val note: String = "",
    val dueDate: LocalDate,
    val paidDate: LocalDate? = null,
    val recurEvery: Int? = null,
    val recurUnit: RecurUnit? = null,
    val historyCount: Int = 0
) {
    val isRecurring: Boolean get() = recurEvery != null && recurUnit != null
    val isPaid: Boolean get() = paidDate != null
    val isLowBalance: Boolean get() = currentBalance <= minBalance
}
