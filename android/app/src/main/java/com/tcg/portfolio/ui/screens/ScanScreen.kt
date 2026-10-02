package com.tcg.portfolio.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tcg.portfolio.data.model.ScanResponse
import com.tcg.portfolio.data.repository.PortfolioRepository
import com.tcg.portfolio.data.network.ApiClient
import com.tcg.portfolio.navigation.Screen
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var scanResult by remember { mutableStateOf<ScanResponse?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val repository = remember { PortfolioRepository(ApiClient.apiService) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            val file = File(imageUri!!.path ?: "")
            val bytes = file.readBytes()
            isScanning = true
            scanResult = null
            val result = repository.scanCard(bytes)
            scanResult = result
            isScanning = false
        }
    }

    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        hasPermission = permission == PackageManager.PERMISSION_GRANTED
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear Carta") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!hasPermission) {
                Text(
                    text = "Se necesita permiso de cámara para escanear cartas",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Conceder permiso")
                }
            } else if (imageUri == null && scanResult == null) {
                Text(
                    text = "Toca la cámara para fotografiar una carta",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val photoFile = createImageFile(context)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        imageUri = uri
                        takePictureLauncher.launch(uri)
                    },
                    modifier = Modifier.size(200.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Capturar",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            if (imageUri != null && scanResult == null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Carta escaneada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                if (isScanning) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }

            if (scanResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                ScanResultCard(result = scanResult!!)
            }
        }
    }
}

private fun createImageFile(context: android.content.Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "TCG_SCAN_${timeStamp}",
        ".jpg",
        storageDir
    )
}

@Composable
fun ScanResultCard(result: ScanResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (result.success) {
                Text(
                    text = result.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow("Set", result.setName)
                InfoRow("Código", result.setCode)
                InfoRow("Número", result.cardNumber)
                InfoRow("Rareza", result.rarity ?: "N/A")
                if (result.variant != null) {
                    InfoRow("Variante", result.variant)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* TODO: navigate to add to collection */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Añadir a colección")
                    }
                    OutlinedButton(
                        onClick = { /* TODO: rescan */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reescanear")
                    }
                }
            } else {
                Text(
                    text = "No se pudo identificar la carta",
                    style = MaterialTheme.typography.bodyLarge
                )
                result.message?.let { msg ->
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}