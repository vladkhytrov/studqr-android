package com.vladkhytrov.studpresence.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vladkhytrov.studpresence.R
import com.vladkhytrov.studpresence.api.Api
import com.vladkhytrov.studpresence.data.Role
import com.vladkhytrov.studpresence.data.TokenStorage
import com.vladkhytrov.studpresence.data.UserStorage
import com.vladkhytrov.studpresence.databinding.ActivityRegistrationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    private val tokenStorage by lazy { TokenStorage(this) }
    private val userStorage by lazy { UserStorage(this) }
    private val api = Api.instance

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
        val firstName = binding.firstName.editText!!.text.toString().trim()
        val lastName = binding.lastName.editText!!.text.toString().trim()
        //val studentId = binding.studentId.editText!!.text.toString().trim()
        val email = binding.email.editText!!.text.toString().trim()
        val password = binding.password.editText!!.text.toString().trim()

        val selected = binding.radioGroup.checkedRadioButtonId
        val selectedRole = if (selected == binding.radioStudent.id) {
            Role.STUDENT.roleName
        } else {
            Role.TEACHER.roleName
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = api.register(firstName, lastName, selectedRole, email, password)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val body = response.body()!!

                    val token = body.get("token").asString
                    tokenStorage.saveToken(token)

                    val user = body.get("user").asJsonObject
                    userStorage.saveUser(user)
                    val role = user.get("role").asString
                    Log.d("test", "role: $role")

                    when (role) {
                        Role.STUDENT.roleName -> {
                            startStudentActivity()
                        }
                        Role.TEACHER.roleName -> {
                            startTeacherActivity()
                        }
                        else -> {
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Invalid role",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@RegistrationActivity,
                        R.string.error_registration_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                    // todo set error to edittext
                }
            }
        }
    }

    private fun startStudentActivity() {
        val intent = Intent(this, StudentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun startTeacherActivity() {
        val intent = Intent(this, TeacherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}