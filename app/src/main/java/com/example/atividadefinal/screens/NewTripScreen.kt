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

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nova viagem", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuTripType(selected = type, onTypeChange = { type = it })

        DatePickerField(label = "Data de Início", date = startDate, dateFormatter = dateFormatter) { selectedDate ->
            startDate = selectedDate
        }

        DatePickerField(label = "Data Final", date = endDate, dateFormatter = dateFormatter) { selectedDate ->
            endDate = selectedDate
        }

        CurrencyTextField(value = budget, onValueChange = { budget = it })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && budget.isNotBlank()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tripDao.insertTrip(
                                Trip(
                                    destino = destination,
                                    tipo = type,
                                    dataInicio = startDate,
                                    dataFinal = endDate,
                                    orcamento = budget.replace("[R$,.\\s]".toRegex(), "").toDouble() / 100,
                                )
                            )
                        }
                        Toast.makeText(context, "Viagem salva com sucesso!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Salvar")
            }

            Button(
                onClick = {
                    destination = ""
                    type = "Lazer"
                    startDate = ""
                    endDate = ""
                    budget = ""
                    Toast.makeText(context, "Campos limpos!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Limpar campos"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Limpar campos")
            }
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
fun CurrencyTextField(value: String, onValueChange: (String) -> Unit) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    // Controla o valor interno de centavos como string de números
    var internalValue by remember { mutableStateOf(value.ifEmpty { "0" }) }

    OutlinedTextField(
        value = currencyFormatter.format(internalValue.toLong() / 100.0),
        onValueChange = { newText ->
            // Extrai apenas números
            val digits = newText.replace(Regex("[^\\d]"), "")

            // Verifica se o campo ficou vazio
            internalValue = if (digits.isEmpty()) {
                "0"
            } else {
                digits
            }

            onValueChange(internalValue)
        },
        label = { Text("Orçamento") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}