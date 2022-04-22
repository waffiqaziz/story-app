package com.dicoding.storyapp

import com.dicoding.storyapp.data.remote.response.*

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

  fun singleDummy(): ListStoryItem {
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

  fun generateDummyListStory(): List<ListStoryItem> {
    val items: MutableList<ListStoryItem> = arrayListOf()
    for (i in 0..100) {
      val story = ListStoryItem(
        "Photo URL Story $i",
        "2022-02-22T22:22:22Z",
        "Story $i ",
        "Story Description",
        "$i",
        0.2,
        0.1
      )
      items.add(story)
    }
    return items
  }

  fun generateDummyLoginResult(): LoginResult {
    return LoginResult(
      "user",
      "1234",
      "token"
    )
  }

  fun generateDummyApiResponseSuccess(): ApiResponse {
    return ApiResponse(
      false,
      "success"
    )
  }

  fun generateDummyLoginResponseSuccess(): LoginResponse {
    return LoginResponse(
      loginResult = generateDummyLoginResult(),
      error = false,
      message = "Success"
    )
  }
}