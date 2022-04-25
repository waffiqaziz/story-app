package com.dicoding.storyapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.di.Injection

class ViewModelFactory private constructor(
  private val storyRepository: StoryRepository
) :
  ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when {
      modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
        ListStoryViewModel(storyRepository) as T
      }
      modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
        MapsViewModel(storyRepository) as T
      }
      modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
        AddStoryViewModel(storyRepository) as T
      }
      modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
        LoginViewModel(storyRepository) as T
      }
      modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
        RegisterViewModel(storyRepository) as T
      }
      else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
  }

  companion object {
    @Volatile
    private var instance: ViewModelFactory? = null
    fun getInstance(context: Context): ViewModelFactory =
      instance ?: synchronized(this) {
        instance ?: ViewModelFactory(Injection.provideStoryRepository(context))
      }.also { instance = it }
  }
}