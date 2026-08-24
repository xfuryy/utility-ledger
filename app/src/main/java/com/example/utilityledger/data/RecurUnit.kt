package com.example.utilityledger.data

import java.time.LocalDate

enum class RecurUnit(val label: String) {
    DAY("Day(s)"),
    WEEK("Week(s)"),
    MONTH("Month(s)"),
    YEAR("Year(s)");

    companion object {
        fun fromStorage(value: String?): RecurUnit? = value?.let { v -> entries.find { it.name == v } }
    }
}

fun LocalDate.plusInterval(every: Int, unit: RecurUnit): LocalDate = when (unit) {
    RecurUnit.DAY -> this.plusDays(every.toLong())
    RecurUnit.WEEK -> this.plusWeeks(every.toLong())
    RecurUnit.MONTH -> this.plusMonths(every.toLong())
    RecurUnit.YEAR -> this.plusYears(every.toLong())
}

fun recurLabel(every: Int, unit: RecurUnit): String {
    val name = when (unit) {
        RecurUnit.DAY -> "day"
        RecurUnit.WEEK -> "week"
        RecurUnit.MONTH -> "month"
        RecurUnit.YEAR -> "year"
    }
    return "every $every $name" + if (every > 1) "s" else ""
}
