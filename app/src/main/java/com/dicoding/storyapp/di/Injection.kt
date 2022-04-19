package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.room.StoryDatabase
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getInstance(context)
        val apiService = ApiConfig.getApiService()
        val dao = database.storyDao()
        return StoryRepository(database,apiService)
    }
}