package com.dicoding.storyapp.data.room

import androidx.paging.PagingSource
import com.dicoding.storyapp.data.remote.response.ListStoryItem

class FakeStoryDao : StoryDao {

  private var storyData = mutableListOf<List<ListStoryItem>>()

  override suspend fun insertStory(storyEntity: List<ListStoryItem>) {
    storyData.add(storyEntity)
  }

  override fun getStory(): PagingSource<Int, ListStoryItem> {
    TODO("Not yet implemented")
  }

  override suspend fun deleteAll() {
    storyData.clear()
  }
}