package com.vladkhytrov.studpresence.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenStorage(context: Context) {

    private val key = "tokenKey"
    private val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        Log.d("test", "saving token: $token")
        val editor = prefs.edit()
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken(): String {
        return prefs.getString("token", "").orEmpty()
    }

    fun getTokenBearer(): String {
        return "Bearer ${getToken()}"
    }

}