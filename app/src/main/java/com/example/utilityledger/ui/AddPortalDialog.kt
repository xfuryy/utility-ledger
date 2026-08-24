package com.example.utilityledger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utilityledger.data.RecurUnit
import com.example.utilityledger.ui.components.DateField
import com.example.utilityledger.ui.components.Recurrence
import com.example.utilityledger.ui.components.RecurrenceField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPortalDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String, minBalance: Double, currentBalance: Double,
        dueDate: LocalDate, note: String, recurEvery: Int?, recurUnit: RecurUnit?
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var minBalanceText by remember { mutableStateOf("") }
    var currentBalanceText by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(LocalDate.now()) }
    var note by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf(Recurrence(false, 1, RecurUnit.MONTH)) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a portal") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(name, { name = it }, label = { Text("Portal name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(minBalanceText, { minBalanceText = it }, label = { Text("Minimum balance (company set)") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                OutlinedTextField(currentBalanceText, { currentBalanceText = it }, label = { Text("Current balance") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                DateField("Next recharge due", dueDate, { dueDate = it }, modifier = Modifier.fillMaxWidth())
                RecurrenceField(recurrence) { recurrence = it }
                OutlinedTextField(note, { note = it }, label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                error?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val minBal = minBalanceText.toDoubleOrNull()
                val curBal = currentBalanceText.toDoubleOrNull()
                if (name.isBlank() || minBal == null || minBal < 0 || curBal == null || curBal < 0) {
                    error = "Enter a portal name, minimum balance, and current balance."
                    return@TextButton
                }
                onConfirm(
                    name, minBal, curBal, dueDate, note,
                    if (recurrence.enabled) recurrence.every else null,
                    if (recurrence.enabled) recurrence.unit else null
                )
            }) { Text("Add portal") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
