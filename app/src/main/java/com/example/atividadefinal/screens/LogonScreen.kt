package com.example.atividadefinal.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atividadefinal.database.AppDatabase
import com.example.atividadefinal.database.User
import kotlinx.coroutines.launch
// ViewModel para lidar com o Banco de Dados
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel


@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var erroEmail by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastro", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nome de usuário") })

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome completo") })

        OutlinedTextField(value = email,
            onValueChange = {
                email = it
                erroEmail = !isValidEmail(email) // Valida o e-mail
                },

            label = { Text("E-mail") })

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Senha") },
            visualTransformation = PasswordVisualTransformation()
        )

        if (erroEmail) Text("E-mail inválido", color = Color.Red, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (!isValidEmail(email)) {
                Toast.makeText(context, "E-mail inválido!", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (password != confirmPassword) {
                Toast.makeText(context, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (username.isNotBlank() && name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                viewModel.registerUser(context, User(username = username, name = name, email = email, password = password)) { sucesso, mensagem ->
                    Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show()
                    if (sucesso) {
                        navController.navigate("login")
                    }
                }
            } else {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Registrar")
        }
    }
}

class RegisterViewModel : ViewModel() {
    fun registerUser(context: Context, user: User, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context).userDao()

            // Verifica se já existe um usuário com o mesmo nome de usuário ou e-mail
            val userByUsername = db.getByUsername(user.username)
            val userByEmail = db.getByEmail(user.email)

            if (userByUsername != null) {
                onResult(false, "Nome de usuário já cadastrado!")
            } else if (userByEmail != null) {
                onResult(false, "E-mail já cadastrado!")
            } else {
                db.insertUser(user)
                onResult(true, "Usuário cadastrado com sucesso!")
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}