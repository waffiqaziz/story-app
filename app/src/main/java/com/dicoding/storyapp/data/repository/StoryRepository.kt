package com.dicoding.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.remote.StoryRemoteMediator
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.LoginResult
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.data.room.StoryDatabase
import com.dicoding.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
  private val storyDatabase: StoryDatabase,
  private val apiService: ApiService,
) {

  fun register(name: String, email: String, pass: String): LiveData<ResultResponse<ApiResponse>> =
    liveData {
      emit(ResultResponse.Loading)
      try {
        val response = apiService.register(name, email, pass)
        if (!response.error) {
          emit(ResultResponse.Success(response))
        } else {
          Log.e(TAG, "Register Fail: ${response.message}")
          emit(ResultResponse.Error(response.message))
        }
      } catch (e: Exception) {
        Log.e(TAG, "Register Exception: ${e.message.toString()} ")
        emit(ResultResponse.Error(e.message.toString()))
      }
    }

  fun login(email: String, pass: String): LiveData<ResultResponse<LoginResult>> =
    liveData {
      emit(ResultResponse.Loading)
      try {
        val response = apiService.login(email, pass)
        if (!response.error) {
          emit(ResultResponse.Success(response.loginResult))
        } else {
          Log.e(TAG, "Register Fail: ${response.message}")
          emit(ResultResponse.Error(response.message))
        }
      } catch (e: Exception) {
        Log.e(TAG, "Register Exception: ${e.message.toString()} ")
        emit(ResultResponse.Error(e.message.toString()))
      }
    }

  fun getStoryMap(token: String): LiveData<ResultResponse<List<ListStoryItem>>> =
    liveData {
      emit(ResultResponse.Loading)
      try {
        val response = apiService.getAllStoriesLocation("Bearer $token")
        if (!response.error) {
          emit(ResultResponse.Success(response.listStory))
        } else {
          Log.e(TAG, "GetStoryMap Fail: ${response.message}")
          emit(ResultResponse.Error(response.message))
        }

      } catch (e: Exception) {
        Log.e(TAG, "GetStoryMap Exception: ${e.message.toString()} ")
        emit(ResultResponse.Error(e.message.toString()))
      }
    }

  fun postStory(
    token: String,
    description: RequestBody,
    imageMultipart: MultipartBody.Part,
    lat: RequestBody? = null,
    lon: RequestBody? = null
  ): LiveData<ResultResponse<ApiResponse>> = liveData {
    emit(ResultResponse.Loading)
    try {
      val response = apiService.addStories("Bearer $token", description, imageMultipart, lat, lon)
      if (!response.error) {
        emit(ResultResponse.Success(response))
      } else {
        Log.e(TAG, "PostStory Fail: ${response.message}")
        emit(ResultResponse.Error(response.message))
      }
    } catch (e: Exception) {
      Log.e(TAG, "PostStory Exception: ${e.message.toString()} ")
      emit(ResultResponse.Error(e.message.toString()))
    }
  }

  fun getPagingStories(token: String): Flow<PagingData<ListStoryItem>> {
    wrapEspressoIdlingResource {
      @OptIn(ExperimentalPagingApi::class)
      return Pager(
        config = PagingConfig(
          pageSize = 5
        ),
        remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
        pagingSourceFactory = {
          storyDatabase.storyDao().getStory()
        }
      ).flow
    }
  }

  companion object {
    private const val TAG = "StoryRepository"
  }
}