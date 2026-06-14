package com.example.appmobile.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // IP para emulador do Android Studio acessar o localhost do PC: http://10.0.2.2:8000/
    // IP na nuvem (GCP): https://appmobile-api-908144816287.us-central1.run.app/
    private const val BASE_URL = "https://appmobile-api-908144816287.us-central1.run.app/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: GearheadApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        retrofit.create(GearheadApiService::class.java)
    }
}
