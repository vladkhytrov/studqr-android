package com.vladkhytrov.studpresence.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.vladkhytrov.studpresence.data.MsgType
import com.vladkhytrov.studpresence.databinding.ActivityLectureBinding
import okhttp3.*
import okio.ByteString

class LectureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLectureBinding

    private val httpClient = OkHttpClient()
    private lateinit var webSocket: WebSocket

    private val teacherId: Int by lazy {
        intent.extras!!.getInt("teacherId")
    }
    private val lectureId: Int by lazy {
        intent.extras!!.getInt("lectureId")
    }
    private val lectureName: String by lazy {
        intent.extras!!.getString("name")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLectureBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.closeBtn.setOnClickListener {
            finish()
        }
        binding.refreshBtn.setOnClickListener {
            onRefreshClick()
        }

        val localAddr = "ws://10.0.2.2:8070"
        val remoteAddr = "ws://localhost:8070"

        val request = Request.Builder()
            .url(localAddr)
            .build()
        httpClient.newWebSocket(request, listener)
        httpClient.dispatcher.executorService.shutdown()
    }

    private fun onRefreshClick() {
        val json = JsonObject()
        json.addProperty("type", MsgType.QR_REFRESH.value)
        json.addProperty("lecture_id", lectureId)
        webSocket.send(json.toString())
    }

    private val listener: WebSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("test", "WebSocket TEACHER onOpen")

            this@LectureActivity.webSocket = webSocket

            val json = JsonObject()
            json.addProperty("type", MsgType.LECTURE_START.value)
            json.addProperty("teacher_id", teacherId)
            json.addProperty("lecture_id", lectureId)

            webSocket.send(json.toString())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("test", "WebSocket TEACHER MESSAGE text: $text")

            try {
                val json = JsonParser().parse(text).asJsonObject
                val msgType = json.get("type").asString

                if (msgType == MsgType.QR_NEW.value) {
                    // new QR code received

                    runOnUiThread {
                        binding.progress.visibility = View.VISIBLE
                        binding.imgQR.visibility = View.INVISIBLE
                    }

                    // just to look more serious show some loading :)
                    Thread.sleep(500);

                    val qrId = json.get("qr_id").asString

                    val jsonQr = JsonObject()
                    jsonQr.addProperty("qr_id", qrId)

                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.encodeBitmap(
                        jsonQr.toString(),
                        BarcodeFormat.QR_CODE,
                        300,
                        300
                    )

                    runOnUiThread {
                        binding.imgQR.setImageBitmap(bitmap)
                        binding.progress.visibility = View.INVISIBLE
                        binding.imgQR.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("test", "WebSocket TEACHER MESSAGE bytes: " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("test", "WebSocket TEACHER onClosing: $code $reason")
            webSocket.close(1000, null)
            webSocket.cancel()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("test", "WebSocket TEACHER onClosed: $code $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.d("test", "WebSocket TEACHER onFailure: $t")
        }
    }

    private fun disconnect() {
        val json = JsonObject()
        json.addProperty("type", MsgType.LECTURE_START.value)
        json.addProperty("teacher_id", teacherId)
        json.addProperty("lecture_id", lectureId)

        webSocket.send(json.toString())

        webSocket.close(1000, null)
        webSocket.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }

}