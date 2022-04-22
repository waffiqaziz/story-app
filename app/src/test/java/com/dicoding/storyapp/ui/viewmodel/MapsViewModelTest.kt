package com.dicoding.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @Mock
  private lateinit var storyRepository: StoryRepository
  private lateinit var mapsViewModel: MapsViewModel
  private val dummyMaps = DataDummy.generateDummyMapsEntity()

  @Before
  fun setUp() {
    mapsViewModel = MapsViewModel(storyRepository)
  }

  @Test
  fun `when getStories() is Called Should Not Null and Return Success`() {
    val expectedStory = MutableLiveData<ResultResponse<List<ListStoryItem>>>()
    expectedStory.value = ResultResponse.Success(dummyMaps)
    `when`(mapsViewModel.getStories("Token")).thenReturn(expectedStory)

    val actualStory = mapsViewModel.getStories("Token").getOrAwaitValue()

    Mockito.verify(storyRepository).getStoryMap("Token")
    Assert.assertNotNull(actualStory)
    Assert.assertTrue(actualStory is ResultResponse.Success)
    Assert.assertEquals(dummyMaps.size, (actualStory as ResultResponse.Success).data.size)
  }

  @Test
  fun `when Network Error Should Return Error`() {
    val expectedStory = MutableLiveData<ResultResponse<List<ListStoryItem>>>()
    expectedStory.value = ResultResponse.Error("Error")
    `when`(mapsViewModel.getStories("Token")).thenReturn(expectedStory)

    val actualStory = mapsViewModel.getStories("Token").getOrAwaitValue()

    Mockito.verify(storyRepository).getStoryMap("Token")
    Assert.assertNotNull(actualStory)
    Assert.assertTrue(actualStory is ResultResponse.Error)
  }
}