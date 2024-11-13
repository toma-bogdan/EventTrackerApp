package com.example.eventtrackerkotlincompose.qrScanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import com.example.eventtrackerkotlincompose.viewModels.ProfileViewModel
import com.example.eventtrackerkotlincompose.viewModels.QrScannerViewModel
import kotlinx.coroutines.launch


class QrScannerActivity : AppCompatActivity() {

    private val repository by lazy { UserDetailsStore(application) }
    private val eventsRepository by lazy { EventsRepository(HttpService(NetworkClient.client)) }
    private var scanResult by mutableStateOf<String?>(null)

    private val qrScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intentResult: IntentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        Log.d("aaaaaaa", intentResult.toString())
        if (intentResult.contents != null) {
            val ticketCode = intentResult.contents
            Log.d("ticketCode", ticketCode)
            lifecycleScope.launch {
                updateAttendance(ticketCode)
            }
        } else {
            scanResult = "Scan failed or cancelled"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    QrScannerScreen(
                        scanResult = scanResult,
                        onBackPressed = { finish() },
                        onScanAnother = { startQrScanner() }
                    )
                }
            }
        }

        startQrScanner()
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a ticket QR code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        qrScannerLauncher.launch(integrator.createScanIntent())
    }

    private suspend fun updateAttendance(ticketCode: String) {
        val apiService = HttpService(NetworkClient.client)
        val eventsRepository = EventsRepository(apiService)
        repository.getToken.collect { token ->
            if (!token.isNullOrEmpty()) {
                val response = eventsRepository.scanTicket(token, ticketCode)
                scanResult = if (response) {
                    "Ticket successfully scanned!"
                } else {
                    "Ticket already scanned!"
                }
            }
        }
    }
}

@Composable
fun QrScannerScreen(
    scanResult: String?,
    onBackPressed: () -> Unit,
    onScanAnother: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (scanResult != null) {
            Text(scanResult, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                onScanAnother()
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Scan Another")
        }

        Button(onClick = { onBackPressed() }) {
            Text("Back to Main")
        }
    }
}