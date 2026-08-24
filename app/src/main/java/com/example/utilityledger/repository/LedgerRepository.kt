package com.example.utilityledger.repository

import com.example.utilityledger.data.EntryDao
import com.example.utilityledger.data.LedgerEntry
import com.example.utilityledger.data.Portal
import com.example.utilityledger.data.PortalDao
import com.example.utilityledger.data.plusInterval
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class LedgerRepository(
    private val entryDao: EntryDao,
    private val portalDao: PortalDao
) {
    val entries: Flow<List<LedgerEntry>> = entryDao.getAll()
    val portals: Flow<List<Portal>> = portalDao.getAll()

    suspend fun addEntry(entry: LedgerEntry) = entryDao.insert(entry)
    suspend fun addPortal(portal: Portal) = portalDao.insert(portal)

    suspend fun deleteEntry(entry: LedgerEntry) = entryDao.delete(entry)
    suspend fun deletePortal(portal: Portal) = portalDao.delete(portal)

    suspend fun updatePortalBalance(portal: Portal, newBalance: Double) {
        portalDao.update(portal.copy(currentBalance = newBalance))
    }

    /** Toggles paid state for a non-recurring entry, or advances the due date for a recurring one. */
    suspend fun toggleEntryPaid(entry: LedgerEntry) {
        val updated = if (entry.recurEvery != null && entry.recurUnit != null) {
            entry.copy(
                dueDate = entry.dueDate.plusInterval(entry.recurEvery, entry.recurUnit),
                paidDate = null,
                historyCount = entry.historyCount + 1
            )
        } else {
            entry.copy(paidDate = if (entry.paidDate == null) LocalDate.now() else null)
        }
        entryDao.update(updated)
    }

    suspend fun togglePortalPaid(portal: Portal) {
        val updated = if (portal.recurEvery != null && portal.recurUnit != null) {
            portal.copy(
                dueDate = portal.dueDate.plusInterval(portal.recurEvery, portal.recurUnit),
                paidDate = null,
                historyCount = portal.historyCount + 1
            )
        } else {
            portal.copy(paidDate = if (portal.paidDate == null) LocalDate.now() else null)
        }
        portalDao.update(updated)
    }

    suspend fun unpaidEntriesDueBy(date: LocalDate): List<LedgerEntry> =
        entryDao.getAllUnpaidOnce().filter { !it.dueDate.isAfter(date) }

    suspend fun unpaidPortalsDueBy(date: LocalDate): List<Portal> =
        portalDao.getAllUnpaidOnce().filter { !it.dueDate.isAfter(date) }
}
