package com.example.utilityledger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.utilityledger.data.Category
import com.example.utilityledger.ui.components.EntryRow
import com.example.utilityledger.ui.components.PortalRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(viewModel: LedgerViewModel = viewModel()) {
    val entries by viewModel.visibleEntries.collectAsStateWithLifecycle()
    val portals by viewModel.visiblePortals.collectAsStateWithLifecycle()
    val month by viewModel.viewMonth.collectAsStateWithLifecycle()
    val activeCategory by viewModel.activeCategory.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val dueTotal by viewModel.monthDueTotal.collectAsStateWithLifecycle()
    val paidTotal by viewModel.monthPaidTotal.collectAsStateWithLifecycle()

    var showAddEntry by remember { mutableStateOf(false) }
    var showAddPortal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.prevMonth() }) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
                    }
                    Text(month.toString())
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
                    }
                }
            })
        },
        floatingActionButton = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                ExtendedFloatingActionButton(
                    onClick = { showAddPortal = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Portal") }
                )
                ExtendedFloatingActionButton(
                    onClick = { showAddEntry = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Bill") },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = Category.tabs.indexOf(activeCategory)) {
                Category.tabs.forEach { cat ->
                    Tab(
                        selected = activeCategory == cat,
                        onClick = { viewModel.setCategory(cat) },
                        text = { Text(cat) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Due: $currency $dueTotal")
                Text("Paid: $currency $paidTotal")
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
                items(entries, key = { "e_" + it.id }) { entry ->
                    EntryRow(
                        entry = entry,
                        currency = currency,
                        onTogglePaid = { viewModel.toggleEntryPaid(entry) },
                        onDelete = { viewModel.deleteEntry(entry) }
                    )
                }
                items(portals, key = { "p_" + it.id }) { portal ->
                    PortalRow(
                        portal = portal,
                        currency = currency,
                        onTogglePaid = { viewModel.togglePortalPaid(portal) },
                        onDelete = { viewModel.deletePortal(portal) },
                        onBalanceChange = { viewModel.updatePortalBalance(portal, it) }
                    )
                }
            }
        }
    }

    if (showAddEntry) {
        AddEntryDialog(
            onDismiss = { showAddEntry = false },
            onConfirm = { category, subCategory, name, amount, dueDate, note, recurEvery, recurUnit, minBalance ->
                viewModel.addEntry(category, subCategory, name, amount, dueDate, note, recurEvery, recurUnit, minBalance)
                showAddEntry = false
            }
        )
    }

    if (showAddPortal) {
        AddPortalDialog(
            onDismiss = { showAddPortal = false },
            onConfirm = { name, minBalance, currentBalance, dueDate, note, recurEvery, recurUnit ->
                viewModel.addPortal(name, minBalance, currentBalance, dueDate, note, recurEvery, recurUnit)
                showAddPortal = false
            }
        )
    }
}
