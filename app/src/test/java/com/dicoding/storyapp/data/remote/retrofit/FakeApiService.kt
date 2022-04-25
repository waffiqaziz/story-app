package com.dicoding.storyapp.data.remote.retrofit


import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody


class FakeApiService : ApiService {
  private val dummyStoryResponse = DataDummy.generateDummyStoriesResponse()
  private val dummyApiResponseSuccess = DataDummy.generateDummyApiResponseSuccess()
  private val dummyLoginResponseSuccess =  DataDummy.generateDummyLoginResponseSuccess()

  override suspend fun register(name: String, email: String, pass: String): ApiResponse {
    return dummyApiResponseSuccess
  }

  override suspend fun login(email: String, pass: String): LoginResponse {
    return dummyLoginResponseSuccess
  }

  override suspend fun addStories(
    token: String,
    description: RequestBody,
    file: MultipartBody.Part,
    latitude: RequestBody?,
    longitude: RequestBody?
  ): ApiResponse {
    return dummyApiResponseSuccess
  }

  override suspend fun getAllStories(token: String, page: Int, size: Int): AllStoriesResponse {
    return dummyStoryResponse
  }

  override suspend fun getAllStoriesLocation(token: String): AllStoriesResponse {
    return dummyStoryResponse
  }
}