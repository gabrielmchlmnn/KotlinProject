package com.example.atividadefinal.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.atividadefinal.database.AppDatabase
import com.example.atividadefinal.database.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp


@Composable
fun NewTripScreen(navController: NavController) {
    var destination by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Lazer") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("0") }

    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nova viagem",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            placeholder = { Text("Ex: Paris, França") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuTripType(
            selected = type,
            onTypeChange = { type = it }
        )

        DatePickerField(
            label = "Data de Início",
            date = startDate,
            dateFormatter = dateFormatter
        ) { selectedDate ->
            startDate = selectedDate
        }

        DatePickerField(
            label = "Data Final",
            date = endDate,
            dateFormatter = dateFormatter
        ) { selectedDate ->
            endDate = selectedDate
        }


        CurrencyInputField(budget)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (destination.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                } else {
                    val budgetClean = budget.replace(Regex("[R$\\s.]"), "").replace(",", ".")
                    val budgetValue = budgetClean.toDoubleOrNull() ?: 0.0

                    CoroutineScope(Dispatchers.IO).launch {
                        tripDao.insertTrip(
                            Trip(
                                destino = destination,
                                tipo = type,
                                dataInicio = startDate,
                                dataFinal = endDate,
                                orcamento = budgetValue
                            )
                        )
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Viagem salva com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.navigate("menu")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Salvar", fontSize = 16.sp)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuTripType(selected: String, onTypeChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val tripTypes = listOf("Lazer", "Negócios")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selected,
            onValueChange = { },
            readOnly = true,
            label = { Text("Tipo de Viagem") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tripTypes.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onTypeChange(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DatePickerField(label: String, date: String, dateFormatter: SimpleDateFormat, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(dateFormatter.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = date,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    )
}

@Composable
fun CurrencyInputField(value: String) {
    var rawValue by remember { mutableStateOf(value) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = formatCurrency(rawValue),
        onValueChange = { input ->
            val digits = input.replace(Regex("[^\\d]"), "")
            rawValue = digits
        },
        label = { Text("Orçamento") },
        placeholder = { Text("Ex: R$ 1.000,00") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionRight -> {
                            val currentPos = textFieldValue.selection.end
                            if (currentPos < textFieldValue.text.length) {
                                textFieldValue = textFieldValue.copy(
                                    selection = TextRange(currentPos + 1)
                                )
                            }
                            true
                        }
                        Key.DirectionLeft -> {
                            val currentPos = textFieldValue.selection.start
                            if (currentPos > 0) {
                                textFieldValue = textFieldValue.copy(
                                    selection = TextRange(currentPos - 1)
                                )
                            }
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }

    )
}

// Formata corretamente o valor bruto para moeda brasileira
fun formatCurrency(digits: String): String {
    if (digits.isEmpty()) return ""
    val parsed = digits.toBigDecimalOrNull() ?: return ""
    val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        .format(parsed.divide(100.toBigDecimal()))
    return formatted
}
