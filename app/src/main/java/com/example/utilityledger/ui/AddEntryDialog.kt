package com.example.utilityledger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.utilityledger.data.Category
import com.example.utilityledger.data.RecurUnit
import com.example.utilityledger.ui.components.DateField
import com.example.utilityledger.ui.components.Recurrence
import com.example.utilityledger.ui.components.RecurrenceField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        category: String, subCategory: String, name: String, amount: Double,
        dueDate: LocalDate, note: String, recurEvery: Int?, recurUnit: RecurUnit?, minBalance: Double?
    ) -> Unit
) {
    var category by remember { mutableStateOf(Category.RENT) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var subCategory by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(LocalDate.now()) }
    var note by remember { mutableStateOf("") }
    var minBalanceText by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf(Recurrence(false, 1, RecurUnit.MONTH)) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a bill") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    OutlinedTextField(
                        value = category, onValueChange = {}, readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        Category.addable.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { category = c; categoryExpanded = false })
                        }
                    }
                }
                OutlinedTextField(subCategory, { subCategory = it }, label = { Text("Sub-category") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                OutlinedTextField(amountText, { amountText = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                DateField("Due date", dueDate, { dueDate = it }, modifier = Modifier.fillMaxWidth())
                RecurrenceField(recurrence) { recurrence = it }
                if (category == Category.OTHER) {
                    OutlinedTextField(
                        minBalanceText, { minBalanceText = it },
                        label = { Text("Minimum balance maintained") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
                OutlinedTextField(note, { note = it }, label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                error?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull()
                if (amount == null || amount <= 0.0) {
                    error = "Enter an amount greater than 0."
                    return@TextButton
                }
                onConfirm(
                    category, subCategory, name, amount, dueDate, note,
                    if (recurrence.enabled) recurrence.every else null,
                    if (recurrence.enabled) recurrence.unit else null,
                    if (category == Category.OTHER) minBalanceText.toDoubleOrNull() else null
                )
            }) { Text("Add entry") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
