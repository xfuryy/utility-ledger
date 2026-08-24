package com.example.utilityledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.utilityledger.data.Portal
import com.example.utilityledger.data.recurLabel

@Composable
fun PortalRow(
    portal: Portal,
    currency: String,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit,
    onBalanceChange: (Double) -> Unit
) {
    var balanceText by remember(portal.id, portal.currentBalance) { mutableStateOf(portal.currentBalance.toString()) }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Recharge · Portal balance", fontWeight = FontWeight.Medium)
                Text(portal.name)
                if (portal.note.isNotBlank()) Text(portal.note)
                Text("Min balance: $currency ${portal.minBalance}")
                if (portal.isRecurring) {
                    Text("Repeats " + recurLabel(portal.recurEvery!!, portal.recurUnit!!))
                }
                Text("Due ${portal.dueDate}" + if (portal.isPaid) " · recharged ${portal.paidDate}" else "")
                Text(if (portal.isLowBalance) "Recharge now" else "Balance OK")
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { txt ->
                        balanceText = txt
                        txt.toDoubleOrNull()?.let { onBalanceChange(it) }
                    },
                    label = { Text("Balance") },
                    modifier = Modifier.width(110.dp)
                )
                Row {
                    IconButton(onClick = onTogglePaid) {
                        Icon(
                            if (portal.isPaid && !portal.isRecurring) Icons.Filled.Refresh else Icons.Filled.Check,
                            contentDescription = if (portal.isPaid) "Mark as due" else "Mark as recharged"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove portal")
                    }
                }
            }
        }
    }
}
