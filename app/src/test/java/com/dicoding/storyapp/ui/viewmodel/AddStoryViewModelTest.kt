package com.dicoding.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.getOrAwaitValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File


@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @Mock
  private lateinit var storyRepository: StoryRepository
  private lateinit var addStoryViewModel: AddStoryViewModel
  private val dummyResponseError = "error"
  private val dummyResponse = ApiResponse(
    false,
    "success"
  )

  @Before
  fun setUp() {
    addStoryViewModel = AddStoryViewModel(storyRepository)
  }

  @Test
  fun `when postStory() is called Should Not Null and Return Success`() {
    val file = mock(File::class.java)
    val description = "Description".toRequestBody("text/plain".toMediaType())
    val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    val imageMultipart = MultipartBody.Part.createFormData(
      "image", file.name, requestImageFile
    )
    val latitude = "-6.1755536".toRequestBody("text/plain".toMediaType())
    val longitude = "106.8272159".toRequestBody("text/plain".toMediaType())

    val expectedResponse = MutableLiveData<ResultResponse<ApiResponse>>()
    expectedResponse.value = ResultResponse.Success(dummyResponse)
    `when`(
      addStoryViewModel.postStory(
        "Token",
        description,
        imageMultipart,
        latitude,
        longitude
      )
    ).thenReturn(
      expectedResponse
    )

    val actualResponse =
      addStoryViewModel.postStory("Token", description, imageMultipart, latitude, longitude)
        .getOrAwaitValue()

    Mockito.verify(storyRepository)
      .postStory("Token", description, imageMultipart, latitude, longitude)
    assertNotNull(actualResponse)
    assertTrue(actualResponse is ResultResponse.Success)
    assertEquals(dummyResponse, (actualResponse as ResultResponse.Success).data)
  }

  @Test
  fun `when Network Error Should Return Error`() {
    val file = mock(File::class.java)
    val description = "Description".toRequestBody("text/plain".toMediaType())
    val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    val imageMultipart = MultipartBody.Part.createFormData(
      "image", file.name, requestImageFile
    )

    val expectedResponse = MutableLiveData<ResultResponse<ApiResponse>>()
    expectedResponse.value = ResultResponse.Error(dummyResponseError)
    `when`(addStoryViewModel.postStory("Token", description, imageMultipart)).thenReturn(
      expectedResponse
    )

    val actualResponse =
      addStoryViewModel.postStory("Token", description, imageMultipart).getOrAwaitValue()

    Mockito.verify(storyRepository).postStory("Token", description, imageMultipart)
    assertNotNull(actualResponse)
    assertTrue(actualResponse is ResultResponse.Error)
  }

}