package com.dicoding.storyapp.data.remote.retrofit

import com.dicoding.storyapp.BuildConfig
import com.dicoding.storyapp.BuildConfig.DEBUG
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
  companion object {
    var BASE_URL = BuildConfig.API_URL

    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()

    init {
      if (DEBUG) {
        // Enable logging for debug builds
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
      } else {
        // Disable logging for release builds
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
      }
    }

    fun getApiService(): ApiService {
      val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
      val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
      return retrofit.create(ApiService::class.java)
    }
  }
}