package com.dicoding.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.room.StoryDatabase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

object Injection {
  fun provideStoryRepository(context: Context): StoryRepository {
    val database = StoryDatabase.getInstance(context)
    val apiService = ApiConfig.getApiService()
    return StoryRepository(database, apiService)
  }

  fun provideDatastore(context: Context) : UserPreference{
    return UserPreference.getInstance(context.dataStore)
  }
}