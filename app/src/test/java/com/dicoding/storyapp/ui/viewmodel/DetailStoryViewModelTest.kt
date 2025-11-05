package com.dicoding.storyapp.ui.viewmodel

import com.dicoding.storyapp.utils.DataDummy.singleDummy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailStoryViewModelTest {

  private var detailsViewModel = DetailStoryViewModel()

  @Test
  fun `when setDetailStory() is called Should change storyItem`() {
    val expectedStory = singleDummy()
    detailsViewModel.setDetailStory(singleDummy())

    val actualStory = detailsViewModel.storyItem
    assertNotNull(actualStory)
    assertEquals(actualStory, expectedStory)
  }
}
