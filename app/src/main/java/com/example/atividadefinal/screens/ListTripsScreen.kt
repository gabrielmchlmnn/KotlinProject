package com.example.atividadefinal.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.atividadefinal.database.AppDatabase
import com.example.atividadefinal.database.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ListTripsScreen(navController : NavController) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

    var trips by remember { mutableStateOf(emptyList<Trip>()) }
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    // Carrega as trips na inicialização
    LaunchedEffect(Unit) {
        trips = tripDao.getAllTrips().sortedByDescending { it.id }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Lista de viagens",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        trips.forEach { trip ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Destino: ${trip.destino}", style = MaterialTheme.typography.bodyLarge)
                    Text("Tipo: ${trip.tipo}", style = MaterialTheme.typography.bodyMedium)
                    Text("Início: ${formatDate(trip.dataInicio)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Final: ${formatDate(trip.dataFinal)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Orçamento: R$ ${"%.2f".format(trip.orcamento)}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("editTrip/${trip.id}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)), // Azul
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Editar")
                        }

                        Button(
                            onClick = {
                                tripToDelete = trip
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Vermelho
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Excluir")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excluir")
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmação para excluir
    if (tripToDelete != null) {
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir esta viagem?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            tripToDelete?.let { trip ->
                                tripDao.deleteTrip(trip)
                                // Atualiza lista após deletar
                                trips = tripDao.getAllTrips().sortedByDescending { it.id }
                                tripToDelete = null
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { tripToDelete = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Função para formatar datas no padrão dd/MM/yyyy
fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = parser.parse(dateString)
        date?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}
