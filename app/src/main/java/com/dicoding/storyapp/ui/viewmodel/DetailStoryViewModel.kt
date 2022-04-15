package com.dicoding.storyapp.ui.viewmodel


import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.remote.response.ListStoryItem

class DetailStoryViewModel: ViewModel() {
  lateinit var storyItem: ListStoryItem

  fun setDetailStory(story: ListStoryItem) : ListStoryItem{
    storyItem = story
    return storyItem
  }

}