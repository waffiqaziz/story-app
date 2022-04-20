package com.dicoding.storyapp

import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem

object DataDummy {
  fun generateDummyMapsEntity(): List<ListStoryItem> {
    val storyList = ArrayList<ListStoryItem>()
    for (i in 0..10) {
      val news = ListStoryItem(
        "Photo URL Story $i",
        "2022-02-22T22:22:22Z",
        "Story $i ",
        "Story Description",
        "$i",
        0.2,
        0.1
      )
      storyList.add(news)
    }
    return storyList
  }

  fun singleDummy(): ListStoryItem{
    return ListStoryItem(
      "Photo URL Story 1",
      "2022-02-22T22:22:22Z",
      "Story 1",
      "Story Description",
      "1",
      0.2,
      0.1
    )
  }

  fun generateDummyStoriesResponse(): AllStoriesResponse {
    val storyList = ArrayList<ListStoryItem>()
    for (i in 0..10) {
      val stories = ListStoryItem(
        "Photo URL Story $i",
        "2022-02-22T22:22:22Z",
        "Story $i ",
        "Story Description",
        "$i",
        0.2,
        0.1
      )
      storyList.add(stories)
    }
    return AllStoriesResponse(storyList, false, "Success")
  }

  fun generateDummyStoriesResponsePaging(): List<ListStoryItem> {
    val items: MutableList<ListStoryItem> = arrayListOf()
    for (i in 0..100) {
      val quote = ListStoryItem(
        "Photo URL Story $i",
        "2022-02-22T22:22:22Z",
        "Story $i ",
        "Story Description",
        "$i",
        0.2,
        0.1
      )
      items.add(quote)
    }
    return items
  }
}