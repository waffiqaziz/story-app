package com.dicoding.storyapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.di.Injection

class ListStoryViewModel(
  private val storyRepository: StoryRepository,
  ) : ViewModel() {

  fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
    return storyRepository.getPagingStories(token).cachedIn(viewModelScope)
  }
}

class ListViewModelFactory(private val context: Context) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return ListStoryViewModel(Injection.provideRepository(context)) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
