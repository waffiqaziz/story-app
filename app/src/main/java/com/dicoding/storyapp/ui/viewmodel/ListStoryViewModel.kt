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

  // trigger to refresh the PagingData
  private val refreshTrigger = MutableLiveData<Boolean>().apply { value = true }

  // return new Pager instance whenever refreshTrigger changes.
  fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
    return refreshTrigger.switchMap {
      storyRepository.getPagingStories(token).cachedIn(viewModelScope).asLiveData()
    }
  }

  // force refresh then creating new Pager instance
  fun refreshStories() {
    refreshTrigger.value = !(refreshTrigger.value ?: false)
  }
}
