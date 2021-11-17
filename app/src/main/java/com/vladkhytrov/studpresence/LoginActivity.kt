package com.vladkhytrov.studpresence

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vladkhytrov.studpresence.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.login.setOnClickListener {
            onLoginClick()
        }
        binding.register.setOnClickListener {
            onRegisterClick()
        }
    }

    private fun onLoginClick() {
        val email = binding.email.editText!!.text.toString().trim()
        val password = binding.password.editText!!.text.toString().trim()
    }

    private fun onRegisterClick() {
        // go to register screen
    }
}