package com.vladkhytrov.studpresence.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vladkhytrov.studpresence.adapter.LecturesAdapter
import com.vladkhytrov.studpresence.api.Api
import com.vladkhytrov.studpresence.data.Lecture
import com.vladkhytrov.studpresence.data.TokenStorage
import com.vladkhytrov.studpresence.databinding.ActivityTeacherBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherBinding

    private val api = Api.instance
    private val tokenStorage by lazy { TokenStorage(this) }

    private var items = mutableListOf<Lecture>()
    private lateinit var lecturesAdapter: LecturesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.add.setOnClickListener {
            val intent = Intent(this, AddLectureActivity::class.java)
            startActivityForResult(intent, 1)
        }

        items.add(Lecture(1, 1, "matematica"));

        lecturesAdapter = LecturesAdapter(items, View.OnClickListener {
            onItemClicked((it.tag as RecyclerView.ViewHolder).adapterPosition)
        })
        binding.lecturesList.adapter = lecturesAdapter
        binding.lecturesList.layoutManager = LinearLayoutManager(this)

        loadLectures()
    }

    private fun onItemClicked(position: Int) {
        Log.d("test", "onItemClicked: $position")

        val intent = Intent(this, LectureActivity::class.java)

        intent.putExtra("lectureId", items[position].id)
        intent.putExtra("teacherId", items[position].teacherId)
        intent.putExtra("name", items[position].name)

        startActivity(intent)
    }

    private fun loadLectures() {
        Log.d("test", "loadLectures");
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getLectures(tokenStorage.getTokenBearer())
            if (response.isSuccessful) {
                val json = response.body()!!.asJsonArray
                Log.d("test", "response: \n")
                Log.d("test", json.toString())

                items.clear()

                json.forEach {
                    val id = it.asJsonObject.get("id").asInt
                    val teacherId = it.asJsonObject.get("teacher_id").asInt
                    val name = it.asJsonObject.get("name").asString

                    items.add(
                        Lecture(id, teacherId, name)
                    )
                }

                runOnUiThread {
                    lecturesAdapter.refresh(items)
                }
            } else {
                Toast.makeText(this@TeacherActivity, "Error loading lectures", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("test", "onActivityResult");

        loadLectures()
    }

}