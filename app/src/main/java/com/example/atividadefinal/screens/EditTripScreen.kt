package com.example.atividadefinal.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.atividadefinal.database.AppDatabase
import com.example.atividadefinal.database.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(navController: NavController, tripId: Int) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

    var trip by remember { mutableStateOf<Trip?>(null) }

    var destination by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Lazer") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    LaunchedEffect(tripId) {
        trip = tripDao.getTripById(tripId)
        trip?.let {
            destination = it.destino
            type = it.tipo
            startDate = it.dataInicio
            endDate = it.dataFinal
            budget = it.orcamento.toString()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editar Viagem", style = MaterialTheme.typography.headlineSmall)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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

        Button(
            onClick = {
                if (destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && budget.isNotBlank()) {
                    trip?.let { existingTrip ->
                        val updatedTrip = existingTrip.copy(
                            destino = destination,
                            tipo = type,
                            dataInicio = startDate,
                            dataFinal = endDate,
                            orcamento = budget.toDoubleOrNull() ?: 0.0
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            tripDao.updateTrip(updatedTrip)
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Viagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack() // Volta para a tela anterior
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Alterações")
        }
    }
}
@Composable
fun DatePickerField(label: String, date: String, dateFormatter:String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
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

