package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
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
      remoteMediator = StoryRemoteMediator(storyDatabase, apiService,token),
      pagingSourceFactory = {
        storyDatabase.storyDao().getStory()
      }
    ).liveData
  }
}