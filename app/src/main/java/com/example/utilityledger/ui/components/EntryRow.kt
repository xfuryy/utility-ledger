package com.example.utilityledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.utilityledger.data.LedgerEntry
import com.example.utilityledger.data.recurLabel

@Composable
fun EntryRow(
    entry: LedgerEntry,
    currency: String,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.category + if (entry.subCategory.isNotBlank()) " · ${entry.subCategory}" else "",
                    fontWeight = FontWeight.Medium
                )
                Text(entry.name.ifBlank { entry.category })
                if (entry.note.isNotBlank()) Text(entry.note)
                if (entry.minBalance != null) Text("Min balance: $currency ${entry.minBalance}")
                if (entry.isRecurring) {
                    Text("Repeats " + recurLabel(entry.recurEvery!!, entry.recurUnit!!))
                }
                Text("Due ${entry.dueDate}" + if (entry.isPaid) " · paid ${entry.paidDate}" else "")
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text("$currency ${entry.amount}", fontWeight = FontWeight.Medium)
                Row {
                    IconButton(onClick = onTogglePaid) {
                        Icon(
                            if (entry.isPaid && !entry.isRecurring) Icons.Filled.Refresh else Icons.Filled.Check,
                            contentDescription = if (entry.isPaid) "Mark as due" else "Mark as paid"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete entry")
                    }
                }
            }
        }
    }
}
