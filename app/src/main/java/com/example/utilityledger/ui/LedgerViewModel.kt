package com.example.utilityledger.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.utilityledger.data.AppDatabase
import com.example.utilityledger.data.Category
import com.example.utilityledger.data.LedgerEntry
import com.example.utilityledger.data.Portal
import com.example.utilityledger.data.RecurUnit
import com.example.utilityledger.data.SettingsDataStore
import com.example.utilityledger.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = LedgerRepository(db.entryDao(), db.portalDao())
    private val settings = SettingsDataStore(application)

    private val _viewMonth = MutableStateFlow(YearMonth.now())
    val viewMonth: StateFlow<YearMonth> = _viewMonth.asStateFlow()

    private val _activeCategory = MutableStateFlow(Category.ALL)
    val activeCategory: StateFlow<String> = _activeCategory.asStateFlow()

    val currency: StateFlow<String> = settings.currency.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "Rs."
    )
    val reminderEmail: StateFlow<String> = settings.reminderEmail.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )

    private val allEntries = repo.entries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val allPortals = repo.portals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visibleEntries: StateFlow<List<LedgerEntry>> = combine(allEntries, _viewMonth, _activeCategory) { entries, month, cat ->
        entries.filter { YearMonth.from(it.dueDate) == month && (cat == Category.ALL || it.category == cat) }
            .sortedBy { it.dueDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visiblePortals: StateFlow<List<Portal>> = combine(allPortals, _viewMonth, _activeCategory) { portals, month, cat ->
        if (cat != Category.ALL && cat != Category.RECHARGE) return@combine emptyList()
        portals.filter { YearMonth.from(it.dueDate) == month }.sortedBy { it.dueDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthDueTotal: StateFlow<Double> = combine(allEntries, _viewMonth) { entries, month ->
        entries.filter { YearMonth.from(it.dueDate) == month && it.paidDate == null }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthPaidTotal: StateFlow<Double> = combine(allEntries, _viewMonth) { entries, month ->
        entries.filter { YearMonth.from(it.dueDate) == month && it.paidDate != null }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setMonth(month: YearMonth) { _viewMonth.value = month }
    fun prevMonth() { _viewMonth.value = _viewMonth.value.minusMonths(1) }
    fun nextMonth() { _viewMonth.value = _viewMonth.value.plusMonths(1) }
    fun setCategory(cat: String) { _activeCategory.value = cat }

    fun setCurrency(value: String) = viewModelScope.launch { settings.setCurrency(value) }
    fun setReminderEmail(value: String) = viewModelScope.launch { settings.setReminderEmail(value) }

    fun addEntry(
        category: String,
        subCategory: String,
        name: String,
        amount: Double,
        dueDate: LocalDate,
        note: String,
        recurEvery: Int?,
        recurUnit: RecurUnit?,
        minBalance: Double?
    ) = viewModelScope.launch {
        repo.addEntry(
            LedgerEntry(
                category = category,
                subCategory = subCategory,
                name = name,
                amount = amount,
                dueDate = dueDate,
                note = note,
                recurEvery = recurEvery,
                recurUnit = recurUnit,
                minBalance = minBalance
            )
        )
    }

    fun addPortal(
        name: String,
        minBalance: Double,
        currentBalance: Double,
        dueDate: LocalDate,
        note: String,
        recurEvery: Int?,
        recurUnit: RecurUnit?
    ) = viewModelScope.launch {
        repo.addPortal(
            Portal(
                name = name,
                minBalance = minBalance,
                currentBalance = currentBalance,
                dueDate = dueDate,
                note = note,
                recurEvery = recurEvery,
                recurUnit = recurUnit
            )
        )
    }

    fun toggleEntryPaid(entry: LedgerEntry) = viewModelScope.launch { repo.toggleEntryPaid(entry) }
    fun togglePortalPaid(portal: Portal) = viewModelScope.launch { repo.togglePortalPaid(portal) }
    fun deleteEntry(entry: LedgerEntry) = viewModelScope.launch { repo.deleteEntry(entry) }
    fun deletePortal(portal: Portal) = viewModelScope.launch { repo.deletePortal(portal) }
    fun updatePortalBalance(portal: Portal, newBalance: Double) = viewModelScope.launch {
        repo.updatePortalBalance(portal, newBalance)
    }
}
