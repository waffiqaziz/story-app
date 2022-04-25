package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
  fun postStory(
    token: String,
    description: RequestBody,
    imageMultipart: MultipartBody.Part,
    lat: RequestBody? = null,
    lon: RequestBody? = null
  ) = storyRepository.postStory(token, description, imageMultipart, lat, lon)
}