package com.dicoding.storyapp.data.remote.retrofit

import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
  @FormUrlEncoded
  @POST("register")
  suspend fun register(
    @Field("name") name: String,
    @Field("email") email: String,
    @Field("password") pass: String,
  ): ApiResponse

  @FormUrlEncoded
  @POST("login")
  suspend fun login(
    @Field("email") email: String,
    @Field("password") pass: String,
  ): LoginResponse

  @Multipart
  @POST("stories")
  suspend fun addStories(
    @Header("Authorization") token: String,
    @Part("description") description: RequestBody,
    @Part file: MultipartBody.Part,
    @Part("lat") latitude: RequestBody?,
    @Part("lon") longitude: RequestBody?,
  ): ApiResponse

  @GET("stories")
  suspend fun getAllStories(
    @Header("Authorization") token: String,
    @Query("page") page: Int,
    @Query("size") size: Int,
  ): AllStoriesResponse

  @GET("stories?location=1")
  suspend fun getAllStoriesLocation(
    @Header("Authorization") token: String,
  ): AllStoriesResponse
}
