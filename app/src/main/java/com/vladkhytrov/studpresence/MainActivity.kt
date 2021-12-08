package com.vladkhytrov.studpresence

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.zxing.integration.android.IntentIntegrator
import com.vladkhytrov.studpresence.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.scan.setOnClickListener {
            onScanClick()
        }
    }

    private fun onScanClick() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(
            listOf(IntentIntegrator.QR_CODE)
        )
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, data)
        if (result != null) {
            binding.text.text = result.contents
        }
    }
}