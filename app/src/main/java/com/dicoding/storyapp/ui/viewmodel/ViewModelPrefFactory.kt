package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.model.UserPreference

class ViewModelPrefFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when {
      modelClass.isAssignableFrom(MainViewModel::class.java) -> {
        MainViewModel(pref) as T
      }
      modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
        SignInViewModel(pref) as T
      }
      else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
  }
}