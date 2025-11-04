package com.dicoding.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.MainCoroutineRule
import com.dicoding.storyapp.utils.PagedTestDataSource
import com.dicoding.storyapp.utils.TestUtils.getOrAwaitValue
import com.dicoding.storyapp.utils.TestUtils.noopListUpdateCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var mainCoroutineRules = MainCoroutineRule()

  @Mock
  private lateinit var listStoryViewModel: ListStoryViewModel

  @Test
  fun `when Get Story Should Not Null`() = runTest {
    val dummyStory = DataDummy.generateDummyListStory()
    val data = PagedTestDataSource.snapshot(dummyStory)
    val story = MutableLiveData<PagingData<ListStoryItem>>()
    story.value = data

    Mockito.`when`(listStoryViewModel.getStory("token")).thenReturn(story)
    val actualStory = listStoryViewModel.getStory("token").getOrAwaitValue()

    val differ = AsyncPagingDataDiffer(
      diffCallback = StoryAdapter.DIFF_CALLBACK,
      updateCallback = noopListUpdateCallback,
      mainDispatcher = mainCoroutineRules.dispatcher,
      workerDispatcher = mainCoroutineRules.dispatcher,
    )

    differ.submitData(actualStory)

    advanceUntilIdle()
    Mockito.verify(listStoryViewModel).getStory("token")
    Assert.assertNotNull(differ.snapshot())
    Assert.assertEquals(dummyStory.size, differ.snapshot().size)
    Assert.assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
  }

  @Test
  fun `when Get Story With No Data Should Return Zero Items`() = runTest {
    val emptyStoryList = emptyList<ListStoryItem>()
    val data = PagedTestDataSource.snapshot(emptyStoryList)
    val story = MutableLiveData<PagingData<ListStoryItem>>()
    story.value = data

    Mockito.`when`(listStoryViewModel.getStory("token")).thenReturn(story)
    val actualStory = listStoryViewModel.getStory("token").getOrAwaitValue()

    val differ = AsyncPagingDataDiffer(
      diffCallback = StoryAdapter.DIFF_CALLBACK,
      updateCallback = noopListUpdateCallback,
      mainDispatcher = mainCoroutineRules.dispatcher,
      workerDispatcher = mainCoroutineRules.dispatcher,
    )

    differ.submitData(actualStory)

    advanceUntilIdle()
    Mockito.verify(listStoryViewModel).getStory("token")
    Assert.assertNotNull(differ.snapshot())
    Assert.assertEquals(0, differ.snapshot().size) // zero item
  }
}
