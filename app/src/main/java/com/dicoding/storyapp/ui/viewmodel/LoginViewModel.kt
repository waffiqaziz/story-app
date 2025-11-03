package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.launch

class LoginViewModel(
  private val storyRepository: StoryRepository,
  private val userPreference: UserPreference,
) : ViewModel() {

  fun login(email: String, pass: String) =
    storyRepository.login(email, pass)

  fun saveUser(user: UserModel) {
    viewModelScope.launch {
      wrapEspressoIdlingResource {
        userPreference.saveUser(user)
      }
    }
  }
}