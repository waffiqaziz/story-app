package com.dicoding.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.MainCoroutineRule
import com.dicoding.storyapp.utils.TestUtils.getOrAwaitValue
import com.dicoding.storyapp.utils.TestUtils.noopListUpdateCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {

  private val token = "token"

  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var mainCoroutineRules = MainCoroutineRule()

  @Mock
  private lateinit var storyRepository: StoryRepository

  private lateinit var listStoryViewModel: ListStoryViewModel

  @Before
  fun setup() {
    // set default data to avoid null pointer exception
    val story = flowOf(PagingData.from(emptyList<ListStoryItem>()))
    `when`(storyRepository.getPagingStories(token)).thenReturn(story)
    listStoryViewModel = ListStoryViewModel(storyRepository)
  }

  @Test
  fun `when Get Story Should Not Null`() = runTest {
    val dummyStory = DataDummy.generateDummyListStory()
    val dataFlow = flowOf(PagingData.from(dummyStory))
    `when`(storyRepository.getPagingStories(token)).thenReturn(dataFlow)

    clearInvocations(storyRepository)

    val actualStory = listStoryViewModel.getStory(token).getOrAwaitValue()
    val differ = AsyncPagingDataDiffer(
      diffCallback = StoryAdapter.DIFF_CALLBACK,
      updateCallback = noopListUpdateCallback,
      mainDispatcher = mainCoroutineRules.dispatcher,
      workerDispatcher = mainCoroutineRules.dispatcher,
    )

    differ.submitData(actualStory)

    advanceUntilIdle()
    // safe warning
    // https://youtrack.jetbrains.com/projects/KTIJ/issues/KTIJ-34798/K2-False-positive-inspection-Flow-is-constructed-but-not-used-with-Mockito
    verify(storyRepository, atLeastOnce()).getPagingStories(token)
    assertNotNull(differ.snapshot())
    assertEquals(dummyStory.size, differ.snapshot().size)
    assertEquals(dummyStory[0], differ.snapshot()[0])
  }


  @Test
  fun `when Get Story With No Data Should Return Zero Items`() = runTest {
    val emptyStoryList = emptyList<ListStoryItem>()
    val dataFlow = flowOf(PagingData.from(emptyStoryList))
    `when`(storyRepository.getPagingStories(token)).thenReturn(dataFlow)

    clearInvocations(storyRepository)

    val actualStory = listStoryViewModel.getStory(token).getOrAwaitValue()
    val differ = AsyncPagingDataDiffer(
      diffCallback = StoryAdapter.DIFF_CALLBACK,
      updateCallback = noopListUpdateCallback,
      mainDispatcher = mainCoroutineRules.dispatcher,
      workerDispatcher = mainCoroutineRules.dispatcher,
    )

    differ.submitData(actualStory)

    advanceUntilIdle()
    verify(storyRepository, atLeastOnce()).getPagingStories(token)
    assertNotNull(differ.snapshot())
    assertEquals(0, differ.snapshot().size) // zero item
  }

  @Test
  fun `when Refresh Stories Should Trigger New Paging Data`() = runTest {
    clearInvocations(storyRepository)

    val dummyStory = DataDummy.generateDummyListStory()
    val dataFlow = flowOf(PagingData.from(dummyStory))
    `when`(storyRepository.getPagingStories(token)).thenReturn(dataFlow)

    listStoryViewModel.getStory(token).getOrAwaitValue()
    listStoryViewModel.refreshStories()
    advanceUntilIdle()

    // should trigger twice
    verify(storyRepository, times(2)).getPagingStories(token)
  }
}
