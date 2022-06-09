package com.vladkhytrov.studpresence.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vladkhytrov.studpresence.api.Api
import com.vladkhytrov.studpresence.data.TokenStorage
import com.vladkhytrov.studpresence.databinding.ActivityAddLectureBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddLectureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLectureBinding

    private val api = Api.instance
    private val tokenStorage by lazy { TokenStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLectureBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.confirmBtn.setOnClickListener {
            onConfirmClick()
        }
    }

    private fun onConfirmClick() {
        val eventName = binding.lectureName.editText!!.text.toString().trim()

        CoroutineScope(Dispatchers.IO).launch {
            val response = api.createLecture(tokenStorage.getTokenBearer(), eventName)
            if (response.isSuccessful) {
                setResult(Activity.RESULT_OK, Intent())
                finish()
            } else {
                // todo show toast error
                setResult(Activity.RESULT_CANCELED, Intent())
                finish()
            }
        }
    }

}