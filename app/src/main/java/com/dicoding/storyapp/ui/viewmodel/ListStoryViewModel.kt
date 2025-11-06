package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.repository.StoryRepository

class ListStoryViewModel(
  private val storyRepository: StoryRepository,
) : ViewModel() {

  private val refreshTrigger = MutableLiveData(Unit)

  fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
    return refreshTrigger.switchMap {
      storyRepository.getPagingStories(token).cachedIn(viewModelScope).asLiveData()
    }
  }

  fun refreshStories() {
    refreshTrigger.value = Unit
  }
}
