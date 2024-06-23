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


class QrScannerActivity : AppCompatActivity() {

    private val qrScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intentResult: IntentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        Log.d("aaaaaaa",intentResult.toString())
        if (intentResult.contents != null) {
            val ticketCode = intentResult.contents
            Log.d("ticketCode",ticketCode)
            updateAttendance(ticketCode)
        } else {
            // Handle scan failure or cancellation
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    // QR Scanner UI can be added here if needed
                }
            }
        }

        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a ticket QR code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        qrScannerLauncher.launch(integrator.createScanIntent())
    }

    private fun updateAttendance(ticketCode: String) {
        // Call your ViewModel or Repository to handle the network request
        // For example:
        // viewModel.updateAttendance(ticketCode)
    }
}
