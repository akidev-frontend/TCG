package com.tcg.portfolio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tcg.portfolio.data.model.SetInfo
import com.tcg.portfolio.data.model.SetProgress
import com.tcg.portfolio.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val sets by remember { mutableStateOf(emptyList<SetInfo>()) }
    val progress by remember { mutableStateOf(emptyList<SetProgress>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TCG Portfolio") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Collection.route) }) {
                        Icon(Icons.Default.Collections, contentDescription = "Colección")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { /* already here */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Escanear") },
                    label = { Text("Escanear") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Scan.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Collections, contentDescription = "Colección") },
                    label = { Text("Colección") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Collection.route) }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Progreso de Sets",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            items(progress) { progressItem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = progressItem.setName,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${progressItem.ownedCards}/${progressItem.totalCards} cartas",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        LinearProgressIndicator(
                            progress = { progressItem.progressPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${progressItem.progressPercentage}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sets Disponibles",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            items(sets) { set ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    onClick = { navController.navigate(Screen.SetDetail.createRoute(set.id)) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = set.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${set.code} • ${set.totalCards} cartas",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}