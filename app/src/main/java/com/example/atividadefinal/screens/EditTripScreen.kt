package com.example.atividadefinal.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.atividadefinal.components.MyDatePicker
import com.example.atividadefinal.database.AppDatabase
import com.example.atividadefinal.database.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Currency
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTripScreen(navController: NavController, tripId: Int) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

    var trip by remember { mutableStateOf<Trip?>(null) }

    var destination by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Lazer") }
    var startDate by remember { mutableStateOf(LocalDate.MIN) }
    var endDate by remember { mutableStateOf(LocalDate.MIN) }
    var budget by remember { mutableStateOf("0") } // valor em centavos como string

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    LaunchedEffect(tripId) {
        trip = tripDao.getTripById(tripId)
        trip?.let {
            destination = it.destino
            type = it.tipo
            // Tenta fazer parse das datas com segurança
            startDate = try {
                LocalDate.parse(it.dataInicio, formatter)
            } catch (e: Exception) {
                null // ou LocalDate.MIN, dependendo de como trata datas inválidas
            }

            endDate = try {
                LocalDate.parse(it.dataFinal, formatter)
            } catch (e: Exception) {
                null
            }
            budget = (it.orcamento * 100).toInt().toString() // convertendo de volta para string em centavos
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Editar Viagem",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuTripType(selected = type, onTypeChange = { type = it })


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

        CurrencyInputField(value = budget, onValueChange = { budget = it })

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    val cleanBudget = budget.toDoubleOrNull()?.div(100) ?: 0.0

                    val updatedTrip = trip?.copy(
                        destino = destination,
                        tipo = type,
                        dataInicio = startDate.format(formatter),
                        dataFinal = endDate.format(formatter),
                        orcamento = cleanBudget
                    )

                    if (updatedTrip != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tripDao.updateTrip(updatedTrip)
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Viagem atualizada!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Erro ao atualizar viagem.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Salvar")
            }
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

