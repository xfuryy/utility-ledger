package com.example.utilityledger.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.utilityledger.data.RecurUnit

data class Recurrence(val enabled: Boolean, val every: Int, val unit: RecurUnit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceField(value: Recurrence, onChange: (Recurrence) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = value.enabled, onCheckedChange = { onChange(value.copy(enabled = it)) })
        Text("Repeats")
    }
    if (value.enabled) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = value.every.toString(),
                onValueChange = { txt ->
                    val n = txt.toIntOrNull()
                    if (n != null && n > 0) onChange(value.copy(every = n))
                },
                label = { Text("Every") },
                modifier = androidx.compose.ui.Modifier.width(90.dp)
            )
            Spacer(androidx.compose.ui.Modifier.width(8.dp))
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = value.unit.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = androidx.compose.ui.Modifier.menuAnchor()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    RecurUnit.entries.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit.label) },
                            onClick = {
                                onChange(value.copy(unit = unit))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
