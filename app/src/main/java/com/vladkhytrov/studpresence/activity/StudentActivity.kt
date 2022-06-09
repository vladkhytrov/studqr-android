package com.vladkhytrov.studpresence.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.vladkhytrov.studpresence.data.MsgType
import com.vladkhytrov.studpresence.data.UserStorage
import com.vladkhytrov.studpresence.databinding.ActivityStudentBinding
import okhttp3.*
import okio.ByteString

class StudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentBinding

    private val httpClient = OkHttpClient()
    private lateinit var webSocket: WebSocket

    private val userStorage by lazy { UserStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.scan.setOnClickListener {
            onScanClick()
        }

        val localAddr = "ws://10.0.2.2:8070"
        val remoteAddr = "ws://localhost:7070"

        val request = Request.Builder()
            .url(remoteAddr)
            .build()
        httpClient.newWebSocket(request, listener)
        httpClient.dispatcher.executorService.shutdown()
    }

    // Register the launcher and result handler
    private val barcodeLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "Scanned",
                    Toast.LENGTH_LONG
                ).show()

                handleQr(result.contents)
            }
        }

    private fun onScanClick() {
        barcodeLauncher.launch(
            ScanOptions().setOrientationLocked(false)
        )
    }

    private fun handleQr(content: String) {
        val jsonContent = JsonParser().parse(content).asJsonObject

        val qrId = jsonContent.get("qr_id").asString

        val jsonMsg = JsonObject()
        jsonMsg.addProperty("type", MsgType.QR_SCANNED.value)
        jsonMsg.addProperty("qr_id", qrId)
        jsonMsg.addProperty("student_id", userStorage.getId())

        webSocket.send(jsonMsg.toString())
    }

    private val listener: WebSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            this@StudentActivity.webSocket = webSocket

            Log.d("test", "WebSocket STUDENT onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("test", "WebSocket STUDENT MESSAGE text: $text")

            try {
                val json = JsonParser().parse(text).asJsonObject
                val msgType = json.get("type").asString

                if (msgType == MsgType.QR_SCAN_SUCCESS.value) {
                    runOnUiThread {
                        Toast.makeText(
                            this@StudentActivity,
                            "QR registered",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (msgType == MsgType.QR_SCAN_ERROR.value) {
                    runOnUiThread {
                        Toast.makeText(
                            this@StudentActivity,
                            "QR registration error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("test", "MESSAGE bytes: " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
            webSocket.cancel()
            Log.d("test", "WebSocket STUDENT CLOSE: $code $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("test", "WebSocket STUDENT onClosed: $code $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.d("test", "WebSocket STUDENT onFailure: $t")
        }
    }

}