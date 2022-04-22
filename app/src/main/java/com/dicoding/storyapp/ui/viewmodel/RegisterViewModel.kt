package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

  fun register(name: String, email: String, pass: String) =
    storyRepository.register(name,email, pass)

}