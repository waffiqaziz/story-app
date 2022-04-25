package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.model.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference) : ViewModel()  {

  fun getUser(): Flow<UserModel> {
    return pref.getUser()
  }

  fun logout() {
    viewModelScope.launch {
      pref.logout()
    }
  }
}