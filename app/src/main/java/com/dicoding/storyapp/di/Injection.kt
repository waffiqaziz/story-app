package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.room.StoryDatabase

object Injection {
  fun provideRepository(context: Context): StoryRepository {
    val database = StoryDatabase.getInstance(context)
    val apiService = ApiConfig.getApiService()
    return StoryRepository(database, apiService)
  }
}