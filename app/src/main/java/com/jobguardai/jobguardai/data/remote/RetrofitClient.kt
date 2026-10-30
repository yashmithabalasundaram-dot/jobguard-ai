package com.jobguardai.jobguardai.data.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.jobguardai.jobguardai.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Default Android Emulator loopback to host PC XAMPP Apache
    // For physical device testing, change to your PC's local LAN IP (e.g. "http://192.168.1.5/jobguard_ai/backend/api/")
    var BASE_URL = "http://10.0.2.2/jobguard_ai/backend/api/"

    private var appContext: Context? = null
    private var apiServiceInstance: ApiService? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private fun getOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Attach Bearer token if session exists
            appContext?.let { ctx ->
                val token = SessionManager.getInstance(ctx).getAuthToken()
                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }

            requestBuilder.addHeader("Accept", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun getSessionManager(): SessionManager? {
        return appContext?.let { SessionManager.getInstance(it) }
    }

    fun getApiService(): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val instance = retrofit.create(ApiService::class.java)
            apiServiceInstance = instance
            instance
        }
    }
}

