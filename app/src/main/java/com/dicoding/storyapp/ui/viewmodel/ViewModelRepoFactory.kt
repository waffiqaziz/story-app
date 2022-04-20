package com.dicoding.storyapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.di.Injection

class ViewModelRepoFactory private constructor(
  private val storyRepository: StoryRepository) :
  ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
      return ListStoryViewModel(storyRepository) as T
    } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
      return MapsViewModel(storyRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
  }

  companion object {
    @Volatile
    private var instance: ViewModelRepoFactory? = null
    fun getInstance(context: Context): ViewModelRepoFactory =
      instance ?: synchronized(this) {
        instance ?: ViewModelRepoFactory(Injection.provideRepository(context))
      }.also { instance = it }
  }
}