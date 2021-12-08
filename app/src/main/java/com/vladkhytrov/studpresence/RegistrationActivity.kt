package com.vladkhytrov.studpresence

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vladkhytrov.studpresence.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.register.setOnClickListener {
            onRegisterClick()
        }
    }

    private fun onRegisterClick() {
        // go to register screen
    }
}