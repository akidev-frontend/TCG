package com.tcg.portfolio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tcg.portfolio.data.model.CardInfo
import com.tcg.portfolio.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(navController: NavController, cardId: Int) {
    val card by remember { mutableStateOf<CardInfo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Carta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        card?.let { c ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = c.name, style = MaterialTheme.typography.headlineMedium)
                InfoRow("Número", c.number)
                InfoRow("Set ID", c.setId.toString())
                InfoRow("Lenguaje", c.language)
                InfoRow("Rareza", c.rarity ?: "N/A")
                c.variant?.let { InfoRow("Variante", it) }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.AddCollectionEntry.createRoute(c.id))
                    }
                ) {
                    Text("Añadir a colección")
                }
            }
        }
    }
}