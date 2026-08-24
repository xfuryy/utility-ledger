package com.example.utilityledger.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromRecurUnit(unit: RecurUnit?): String? = unit?.name

    @TypeConverter
    fun toRecurUnit(value: String?): RecurUnit? = RecurUnit.fromStorage(value)
}
