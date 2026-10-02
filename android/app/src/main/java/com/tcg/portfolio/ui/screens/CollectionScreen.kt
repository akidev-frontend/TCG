package com.tcg.portfolio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tcg.portfolio.data.model.CollectionEntry
import com.tcg.portfolio.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(navController: NavController) {
    val entries by remember { mutableStateOf(emptyList<CollectionEntry>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Colección") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (entries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay cartas en tu colección",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            items(entries) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Carta ID: ${entry.cardId}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        InfoRow("Cantidad", entry.quantity.toString())
                        InfoRow("Condición", entry.condition ?: "N/A")
                        entry.purchasePrice?.let { price ->
                            InfoRow("Precio", "$${"%.2f".format(price)}")
                        }
                        entry.notes?.let { notes ->
                            InfoRow("Notas", notes)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    navController.navigate(
                                        Screen.AddCollectionEntry.createRoute(entry.cardId)
                                    )
                                }
                            ) {
                                Text("Editar")
                            }
                        }
                    }
                }
            }
        }
    }
}