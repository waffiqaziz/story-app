package com.dicoding.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.remote.StoryRemoteMediator
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.data.room.StoryDatabase

class StoryRepository(
  private val storyDatabase: StoryDatabase,
  private val apiService: ApiService,
) {
  fun getPagingStories(token: String): LiveData<PagingData<ListStoryItem>> {

    @OptIn(ExperimentalPagingApi::class)
    return Pager(
      config = PagingConfig(
        pageSize = 5
      ),
      remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
      pagingSourceFactory = {
        storyDatabase.storyDao().getStory()
      }
    ).liveData
  }

  fun getStoryMap(token: String): LiveData<ResultResponse<List<ListStoryItem>>> = liveData {
    emit(ResultResponse.Loading)
    try {
      val response = apiService.getAllStoriesLocation("Bearer $token")
      if (!response.error) {
        val stories = response.listStory
        emit(ResultResponse.Success(stories))
      } else {
        Log.e(TAG, "FAIL: ${response.message}")
        emit(ResultResponse.Error(response.message))
      }

    } catch (e: Exception) {
      Log.e(TAG, "getStoryMap: ${e.message.toString()} ")
      emit(ResultResponse.Error(e.message.toString()))
    }
  }

  companion object {
    private const val TAG = "StoryRepository"
  }
}