package com.dicoding.storyapp.ui.viewmodel

import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class DetailStoryViewModelTest {
  @Mock
  private lateinit var detailsViewModel: DetailStoryViewModel
  private val dummyStory = DataDummy.singleDummy()

  @Test
  fun `when setDetailStory() is called Should Not Null and Return False`() {
    val expectedStory = dummyStory
    Mockito.`when`(detailsViewModel.setDetailStory(dummyStory)).thenReturn(expectedStory)

    val actualStory = detailsViewModel.setDetailStory(
      ListStoryItem(
        "Photo URL Story 1",
        "2022-02-22T22:22:22Z",
        "Story 1",
        "Story Description",
        "1",
        0.2,
        0.1
      )
    )

    Assert.assertNotNull(actualStory)
    Assert.assertEquals(
      actualStory, expectedStory
    )
  }
}
