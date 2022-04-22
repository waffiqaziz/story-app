package com.dicoding.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.remote.StoryRemoteMediator
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.data.remote.retrofit.FakeApiService
import com.dicoding.storyapp.data.room.FakeStoryDao
import com.dicoding.storyapp.data.room.StoryDao
import com.dicoding.storyapp.data.room.StoryDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @Mock
  private lateinit var storyDatabase: StoryDatabase
  private lateinit var storyRepository: StoryRepository
  private lateinit var apiService: ApiService
  private lateinit var storyDao: StoryDao
  private lateinit var storyRemoteMediator: StoryRemoteMediator
  private var name = "user"
  private var email = "user@email.com"
  private var pass = "userPassword"

  @Before
  fun setUp() {
    apiService = FakeApiService()
    storyDao = FakeStoryDao()
    storyRepository = StoryRepository(storyDatabase, apiService)
    storyRemoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Token")
  }

  @Test
  fun `when register() is called Should  Not Null`() = runTest {
    val expectedResponse = DataDummy.generateDummyApiResponseSuccess()
    val actualResponse = apiService.register(name, email, pass)
    Assert.assertNotNull(actualResponse)
    Assert.assertEquals(expectedResponse, actualResponse)
  }

  @Test
  fun `when login() is called Should  Not Null`() = runTest {
    val expectedResponse = DataDummy.generateDummyLoginResponseSuccess()
    val actualResponse = apiService.login(email, pass)
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
    val file = Mockito.mock(File::class.java)
    val description = "Description".toRequestBody("text/plain".toMediaType())
    val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    val imageMultipart = MultipartBody.Part.createFormData(
      "image", file.name, requestImageFile
    )

    val expectedResponse = DataDummy.generateDummyApiResponseSuccess()
    val actualResponse = apiService.addStories("Token", description, imageMultipart)
    Assert.assertNotNull(actualResponse)
    Assert.assertEquals(expectedResponse, actualResponse)
  }

  @OptIn(ExperimentalPagingApi::class)
  @Test
  fun `when getPagingStories() is called Should Not Null`() = runTest {
    val expectedStory = Pager(config = PagingConfig(pageSize = 3),
      remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "token"),
      pagingSourceFactory = { storyDatabase.storyDao().getStory() }
    ).liveData

    val actualStory = Pager(config = PagingConfig(pageSize = 5),
      remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "token"),
      pagingSourceFactory = { storyDao.getStory() }
    ).liveData

    Assert.assertNotNull(actualStory)
    Assert.assertEquals(expectedStory.value, actualStory.value)
  }
}
