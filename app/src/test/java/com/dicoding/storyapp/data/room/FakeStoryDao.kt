package com.dicoding.storyapp.data.room

import androidx.paging.PagingSource
import com.dicoding.storyapp.data.remote.response.ListStoryItem

class FakeStoryDao : StoryDao {

  private var storyData = mutableListOf<List<ListStoryItem>>()
  private var storyDataPaging = mutableListOf<PagingSource<Int, ListStoryItem>>()

  override suspend fun insertStory(storyEntity: List<ListStoryItem>) {
    storyData.add(storyEntity)
  }

  override fun getStory(): PagingSource<Int, ListStoryItem> {
    return storyDataPaging.first()
  }

  override suspend fun deleteAll() {
    storyData.clear()
  }
}