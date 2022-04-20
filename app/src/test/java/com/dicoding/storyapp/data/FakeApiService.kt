package com.dicoding.storyapp.data


import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call


class FakeApiService : ApiService {
  private val dummyResponse = DataDummy.generateDummyStoriesResponse()

  override fun register(name: String, email: String, pass: String): Call<ApiResponse> {
    TODO("Not yet implemented")
  }

  override fun login(email: String, pass: String): Call<LoginResponse> {
    TODO("Not yet implemented")
  }

  override fun addStories(
    token: String,
    des: RequestBody,
    file: MultipartBody.Part
  ): Call<ApiResponse> {
    TODO("Not yet implemented")
  }

  override suspend fun getAllStories(token: String, page: Int, size: Int): AllStoriesResponse {
    return dummyResponse
  }

  override suspend fun getAllStoriesLocation(token: String): AllStoriesResponse {
    TODO("Not yet implemented")
  }
}