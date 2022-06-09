package com.vladkhytrov.studpresence.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class UserStorage(context: Context) {

    private val key = "userKey"
    private val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)

    fun saveUser(jsonObject: JsonObject) {
        val editor = prefs.edit()
        editor.putString("json", jsonObject.toString())
        editor.apply()
    }

    fun getUserAsJson(): JsonObject {
        val asText = prefs.getString("json", null)!!
        return JsonParser().parse(asText).asJsonObject
    }

    fun saveName(name: String) {
        val editor = prefs.edit()
        editor.putString("name", name)
        editor.apply()
    }

    fun getId(): String {
        return getUserAsJson().get("id").asString
    }

    fun getFirstName(): String {
        return getUserAsJson().get("first_name").asString
    }

    fun getRole(): String {
        return getUserAsJson().get("role").asString
    }

    fun saveRole(role: Role) {
        val editor = prefs.edit()
        editor.putString("role", role.roleName)
        editor.apply()
    }

}