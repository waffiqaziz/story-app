package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.model.UserPreference
import kotlinx.coroutines.launch


class SaveUserViewModel(
  private val pref: UserPreference
) : ViewModel() {

  fun saveUser(user: UserModel) {
    viewModelScope.launch {
      pref.saveUser(user)
    }
  }
}