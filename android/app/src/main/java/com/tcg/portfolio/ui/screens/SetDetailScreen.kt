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
import com.tcg.portfolio.data.model.CardInfo
import com.tcg.portfolio.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailScreen(navController: NavController, setId: Int) {
    val set by remember { mutableStateOf<com.tcg.portfolio.data.model.SetInfo?>(null) }
    val cards by remember { mutableStateOf(emptyList<CardInfo>()) }
    val progress by remember { mutableStateOf<com.tcg.portfolio.data.model.SetProgress?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Set") },
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
            set?.let { s ->
                item {
                    Text(text = s.name, style = MaterialTheme.typography.headlineMedium)
                    InfoRow("Código", s.code)
                    InfoRow("Lenguaje", s.language)
                    InfoRow("Total cartas", s.totalCards.toString())
                }
            }

            progress?.let { p ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Progreso", style = MaterialTheme.typography.titleLarge)
                            LinearProgressIndicator(
                                progress = { p.progressPercentage / 100f },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            )
                            Text(
                                text = "${p.ownedCards}/${p.totalCards} (${p.progressPercentage}%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Text("Cartas del set", style = MaterialTheme.typography.titleLarge)
            }

            items(cards) { card ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    onClick = {
                        navController.navigate(Screen.CardDetail.createRoute(card.id))
                    }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(card.name, style = MaterialTheme.typography.titleLarge)
                        Text("#${card.number}", style = MaterialTheme.typography.bodyMedium)
                        Text(card.rarity ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}