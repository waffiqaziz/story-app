package com.dicoding.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainCoroutineRule
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.data.remote.retrofit.FakeApiService
import com.dicoding.storyapp.data.room.FakeStoryDao
import com.dicoding.storyapp.data.room.StoryDao
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.utils.PagedTestDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @Mock
  private lateinit var storyRepository: StoryRepository
  private lateinit var apiService: ApiService

  @Mock
  private lateinit var storyDao: StoryDao
  private var dummmyName = "user"
  private var dummyEmail = "user@email.com"
  private var dummyPass = "userPassword"
  private var dummyMultipart = DataDummy.generateDummyMultipartFile()
  private var dummyDescription = DataDummy.generateDummyRequestBody()
  private var dummyLatitude = DataDummy.generateDummyRequestBody()
  private var dummyLongitude = DataDummy.generateDummyRequestBody()

  @Before
  fun setUp() {
    apiService = FakeApiService()
    storyDao = FakeStoryDao()
  }

  @Test
  fun `when register() is called Should  Not Null`() = runTest {
    val expectedResponse = DataDummy.generateDummyApiResponseSuccess()
    val actualResponse = apiService.register(dummmyName, dummyEmail, dummyPass)
    Assert.assertNotNull(actualResponse)
    Assert.assertEquals(expectedResponse, actualResponse)
  }

  @Test
  fun `when login() is called Should  Not Null`() = runTest {
    val expectedResponse = DataDummy.generateDummyLoginResponseSuccess()
    val actualResponse = apiService.login(dummyEmail, dummyPass)
    Assert.assertNotNull(actualResponse)
    Assert.assertEquals(expectedResponse, actualResponse)
  }

  @Test
  fun `when getStoryMap() is called Should Not Null`() = runTest {
    val expectedStory = DataDummy.generateDummyStoriesResponse()
    val actualStory = apiService.getAllStoriesLocation("Token")
    Assert.assertNotNull(actualStory)
    Assert.assertEquals(expectedStory.listStory.size, actualStory.listStory.size)
  }

  @Test
  fun `when postStory() is called Should Not Null`() = runTest {
    val expectedResponse = DataDummy.generateDummyApiResponseSuccess()
    val actualResponse = apiService.addStories("Token", dummyDescription, dummyMultipart, dummyLatitude, dummyLongitude)
    Assert.assertNotNull(actualResponse)
    Assert.assertEquals(expectedResponse, actualResponse)
  }

  @OptIn(ExperimentalPagingApi::class)
  @Test
  fun `when getPagingStories() is called Should Not Null`() = runTest {
    val mainCoroutineRule = MainCoroutineRule()

    val dummyStories = DataDummy.generateDummyListStory()
    val data = PagedTestDataSource.snapshot(dummyStories)

    val expectedResult = flowOf(data)
    `when`(storyRepository.getPagingStories("token")).thenReturn(expectedResult)

    storyRepository.getPagingStories("token").collect {
      val differ = AsyncPagingDataDiffer(
        StoryAdapter.DIFF_CALLBACK,
        listUpdateCallback,
        mainCoroutineRule.dispatcher,
        mainCoroutineRule.dispatcher
      )

      differ.submitData(it)
      Assert.assertNotNull(differ.snapshot())
      Assert.assertEquals(
        DataDummy.generateDummyStoriesResponse().listStory.size,
        differ.snapshot().size
      )
    }
  }

  private val listUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
  }
}
