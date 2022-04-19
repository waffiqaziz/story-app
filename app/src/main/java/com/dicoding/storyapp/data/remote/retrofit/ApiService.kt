package com.dicoding.storyapp.data.remote.retrofit

import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
  @FormUrlEncoded
  @POST("register")
  fun register(
    @Field("name") name: String,
    @Field("email") email: String,
    @Field("password") pass: String
  ): Call<ApiResponse>

  @FormUrlEncoded
  @POST("login")
  fun login(
    @Field("email") email: String,
    @Field("password") pass: String
  ): Call<LoginResponse>

  @Multipart
  @POST("stories")
  fun addStories(
    @Header("Authorization") token: String,
    @Part("description") des: RequestBody,
    @Part file: MultipartBody.Part
  ): Call<ApiResponse>

  @GET("stories")
  suspend fun getAllStories(
    @Header("Authorization") token: String,
    @Query("page") page: Int,
    @Query("size") size: Int,
  ): AllStoriesResponse

  @GET("stories")
  fun getAllStories(
    @Header("Authorization") token: String
  ): Call<AllStoriesResponse>
}
