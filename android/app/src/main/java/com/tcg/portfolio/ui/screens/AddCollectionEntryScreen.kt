package com.tcg.portfolio.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollectionEntryScreen(navController: NavController, cardId: Int) {
    var quantity by remember { mutableStateOf("1") }
    var condition by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        val listener = DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, day: Int ->
            purchaseDate = "$year-${month + 1}-$day"
            showDatePicker = false
        }
        DatePickerDialog(
            context,
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir a colección") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Cantidad") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = condition,
                onValueChange = { condition = it },
                label = { Text("Condición") },
                placeholder = { Text("Near Mint, Lightly Played...") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = purchasePrice,
                onValueChange = { purchasePrice = it },
                label = { Text("Precio de compra") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = purchaseDate,
                onValueChange = { purchaseDate = it },
                label = { Text("Fecha de compra") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Text("Seleccionar")
                    }
                }
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}