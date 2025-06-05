package com.example.atividadefinal.screens

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.sp
import com.example.atividadefinal.components.MyDatePicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewTripScreen(navController: NavController) {
    var destination by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Lazer") }
    var startDate by remember { mutableStateOf(LocalDate.MIN) }
    var endDate by remember { mutableStateOf(LocalDate.MIN) }
    var budget by remember { mutableStateOf("0") }

    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
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


        MyDatePicker(
            label = "Data de início",
            value = if (startDate != LocalDate.MIN) startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else "",
            onValueChange = { selectedDate: LocalDate ->
                startDate = selectedDate
            }
        )

        MyDatePicker(
            label = "Data final",
            value = if (endDate != LocalDate.MIN) endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else "",
            onValueChange = { selectedDate: LocalDate ->
                endDate = selectedDate
            }
        )
        CurrencyInputField(value = budget,onValueChange = { budget = it })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (destination.isBlank() || startDate == LocalDate.MIN || endDate == LocalDate.MIN) {
                    Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                } else {
                    val budgetClean = budget.replace(Regex("[R$\\s.]"), "").replace(",", ".")
                    val budgetValue = budgetClean.toDoubleOrNull() ?: 0.0

                    CoroutineScope(Dispatchers.IO).launch {
                        tripDao.insertTrip(
                            Trip(
                                destino = destination,
                                tipo = type,
                                dataInicio = startDate.format(formatter),
                                dataFinal = endDate.format(formatter),
                                orcamento = budgetValue,
                                sugestao  = ""
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
fun DatePickerField(
    label: String,
    date: String,
    dateFormatter: SimpleDateFormat,
    context: Context,
    onDateSelected: (String) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    val today = remember { Calendar.getInstance() }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        // Mostra o DatePickerDialog quando necessário
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormatter.format(calendar.time))
                showDialog = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = today.timeInMillis // Impede datas passadas
            show()
        }
    }

    OutlinedTextField(
        value = date,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    showDialog = true
                }
            }
            .clickable { showDialog = true }
    )
}



@Composable
fun CurrencyInputField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = formatCurrency(value),
        onValueChange = { input ->
            val digits = input.replace(Regex("[^\\d]"), "")
            onValueChange(digits)
        },
        label = { Text("Orçamento") },
        placeholder = { Text("Ex: R$ 1.000,00") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// Formata corretamente o valor bruto (em centavos) para moeda brasileira
fun formatCurrency(digits: String): String {
    if (digits.isEmpty()) return ""
    val parsed = digits.toBigDecimalOrNull() ?: return ""
    val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        .format(parsed.divide(100.toBigDecimal()))
    return formatted
}
