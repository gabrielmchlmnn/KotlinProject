package com.example.atividadefinal.screens

import android.annotation.SuppressLint
import android.app.Notification.MessagingStyle.Message
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.atividadefinal.api.generateSuggestionFromGemini
import kotlinx.coroutines.withContext
import java.text.NumberFormat

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ListTripsScreen(navController: NavController) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var aiSuggestion by remember { mutableStateOf("") }

    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

    var trips by remember { mutableStateOf(emptyList<Trip>()) }
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    LaunchedEffect(Unit) {
        trips = tripDao.getAllTrips().sortedByDescending { it.id }
    }

    fun fetchAiSuggestion(trip:Trip, cidade: String, dataInicio: String, dataFinal: String, tipo: String) {
        isLoading = true
        aiSuggestion = ""
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val suggestion = generateSuggestionFromGemini(cidade, dataFinal, dataInicio, tipo)

                val updatedTrip = trip.copy(sugestao = suggestion)
                tripDao.updateTrip(updatedTrip)

                trips = tripDao.getAllTrips().sortedByDescending { it.id }

                withContext(Dispatchers.Main) {
                    aiSuggestion = suggestion
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    aiSuggestion = "Erro ao gerar sugestão: ${e.message}"
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Minhas viagens",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (trips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma viagem cadastrada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    items(trips) { trip ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Destino: ${trip.destino}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Tipo: ${trip.tipo}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Início: ${formatDate(trip.dataInicio)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Final: ${formatDate(trip.dataFinal)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                val formattedOrcamento = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(trip.orcamento)

                                Text(
                                    "Orçamento: ${formattedOrcamento}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = { navController.navigate("editTrip/${trip.id}") }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { tripToDelete = trip }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            isDialogOpen = true

                                            if (trip.sugestao == ""){
                                                fetchAiSuggestion(trip, trip.destino, trip.dataInicio, trip.dataFinal, trip.tipo)
                                            } else {
                                                aiSuggestion = trip.sugestao
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Ver sugestões")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                FloatingActionButton(
                    onClick = { navController.navigate("new_trip") },

                    modifier = Modifier
                        .align(Alignment.BottomEnd)  // Mudar para o canto inferior direito
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White  // Ajustei a cor do ícone para branco
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova viagem")
                }



                if (isDialogOpen) {
                    AlertDialog(
                        onDismissRequest = { isDialogOpen = false },
                        confirmButton = {
                            TextButton(onClick = { isDialogOpen = false }) {
                                Text("Fechar")
                            }
                        },
                        title = {
                            Text("Sugestões de Destinos")
                        },
                        text = {
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Gerando sugestões...")
                                }
                            } else {
                                // Conteúdo rolável para sugestões longas
                                Box(
                                    modifier = Modifier
                                        .heightIn(max = 300.dp) // Limita a altura do conteúdo para habilitar scroll
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(aiSuggestion)
                                }
                            }
                        }
                    )
                }
            }

        }
        // Diálogo de confirmação de exclusão
        if (tripToDelete != null) {
            AlertDialog(
                onDismissRequest = { tripToDelete = null },
                title = { Text(text = "Excluir viagem") },
                text = { Text(text = "Tem certeza que deseja excluir esta viagem?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                tripToDelete?.let { tripDao.deleteTrip(it) }
                                trips = tripDao.getAllTrips().sortedByDescending { it.id }
                                tripToDelete = null
                            }
                        }
                    ) {
                        Text("Sim", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tripToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

}



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

