package com.vladkhytrov.studpresence.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.vladkhytrov.studpresence.R
import com.vladkhytrov.studpresence.api.Api
import com.vladkhytrov.studpresence.data.Role
import com.vladkhytrov.studpresence.data.TokenStorage
import com.vladkhytrov.studpresence.data.UserStorage
import com.vladkhytrov.studpresence.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val tokenStorage by lazy { TokenStorage(this) }
    private val api = Api.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (tokenStorage.getToken().isNotEmpty()) {
            Log.d("test", "token not empty")
            getUser()
        } else {
            Log.d("test", "token empty")
        }

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

    private fun getUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getUser(tokenStorage.getTokenBearer())

            withContext(Dispatchers.Main) {
                if (response.code() == 200) {

                    val user = response.body()!!
                    saveUserAndNavigate(user)

                } else {
                    Toast.makeText(this@LoginActivity, "Token not valid", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startStudentActivity() {
        val intent = Intent(this, StudentActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startTeacherActivity() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onLoginClick() {
        val email = binding.email.editText!!.text.toString().trim()
        val password = binding.password.editText!!.text.toString().trim()

        CoroutineScope(Dispatchers.IO).launch {
            val response = api.login(email, password)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val body = response.body()!!
                    val token = body.get("token").asString
                    tokenStorage.saveToken(token)

                    saveUserAndNavigate(body.get("user").asJsonObject)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.error_login_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                    // todo set error to edittext
                }
            }
        }
    }

    private fun saveUserAndNavigate(user: JsonObject) {
        val userStorage = UserStorage(this@LoginActivity)
        //userStorage.saveName(user.get("first_name").asString)
        userStorage.saveUser(user)

        val role = userStorage.getRole()
        Log.d("test", "role: $role")
        //userStorage.saveRole(Role.valueOf(role.uppercase()))

        when (role) {
            Role.STUDENT.roleName -> {
                startStudentActivity()
            }
            Role.TEACHER.roleName -> {
                startTeacherActivity()
            }
            else -> {
                Toast.makeText(this@LoginActivity, "Invalid role", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun onRegisterClick() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}