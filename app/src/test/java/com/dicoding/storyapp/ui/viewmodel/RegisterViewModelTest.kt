package com.dicoding.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @Mock
  private lateinit var storyRepository: StoryRepository
  private lateinit var registerViewModel: RegisterViewModel
  private val dummyResponse = DataDummy.generateDummyApiResponseSuccess()

  @Before
  fun setUp() {
    registerViewModel = RegisterViewModel(storyRepository)
  }

  @Test
  fun `when register() is Called Should Not Null and Return Success`() {
    val expectedResponse = MutableLiveData<ResultResponse<ApiResponse>>()
    expectedResponse.value = ResultResponse.Success(dummyResponse)
    Mockito.`when`(registerViewModel.register("Name", "Email", "PasswordUser")).thenReturn(expectedResponse)

    val actualResponse = registerViewModel.register("Name", "Email", "PasswordUser").getOrAwaitValue()

    Mockito.verify(storyRepository).register("Name", "Email", "PasswordUser")
    Assert.assertNotNull(actualResponse)
    Assert.assertTrue(actualResponse is ResultResponse.Success)
    Assert.assertEquals(dummyResponse, (actualResponse as ResultResponse.Success).data)
  }

  @Test
  fun `when Network Error Should Return Error`() {
    val expectedResponse = MutableLiveData<ResultResponse<ApiResponse>>()
    expectedResponse.value = ResultResponse.Error("Error")
    Mockito.`when`(registerViewModel.register("Name", "Email", "PasswordUser")).thenReturn(expectedResponse)

    val actualResponse = registerViewModel.register("Name", "Email", "PasswordUser").getOrAwaitValue()

    Mockito.verify(storyRepository).register("Name", "Email", "PasswordUser")
    Assert.assertNotNull(actualResponse)
    Assert.assertTrue(actualResponse is ResultResponse.Error)
  }
}