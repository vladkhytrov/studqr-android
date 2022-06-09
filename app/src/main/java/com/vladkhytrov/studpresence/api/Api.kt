package com.vladkhytrov.studpresence.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Api {

    companion object {

        val instance: Api by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val headers = Interceptor { chain ->
                val request: Request =
                    chain.request().newBuilder().addHeader("Accept", "application/json").build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(headers)
                .build()
            //http://10.0.2.2:8000/api/

            val localAddr = "http://10.0.2.2:8000/api/"
            val remoteAddr = "http://localhost:3000/api/"

            val retrofit = Retrofit.Builder()
                .baseUrl(localAddr)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
            retrofit.build().create(Api::class.java)
        }

    }

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("role") role: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<JsonObject>

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST("lectures/create")
    suspend fun createLecture(
        @Header("Authorization") token: String,
        @Field("name") name: String
    ): Response<JsonObject>

    @GET("lectures")
    suspend fun getLectures(
        @Header("Authorization") token: String
    ): Response<JsonArray>

    @GET("presences")
    suspend fun getPresences(
        @Header("Authorization") token: String
    ): Response<JsonObject>

}