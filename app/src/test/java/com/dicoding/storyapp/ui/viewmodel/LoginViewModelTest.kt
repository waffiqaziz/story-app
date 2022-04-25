package com.dicoding.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.remote.response.LoginResult
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{
  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @Mock
  private lateinit var storyRepository: StoryRepository
  private lateinit var loginViewModel: LoginViewModel
  private val dummyResult = DataDummy.generateDummyLoginResponseSuccess().loginResult

  @Before
  fun setUp() {
    loginViewModel = LoginViewModel(storyRepository)
  }

  @Test
  fun `when login() is Called Should Return Success and Data`() {
    val expectedResponse = MutableLiveData<ResultResponse<LoginResult>>()
    expectedResponse.value = ResultResponse.Success(dummyResult)
    `when`(loginViewModel.login("Email22", "PasswordUser")).thenReturn(expectedResponse)

    val actualResponse = loginViewModel.login("Email22", "PasswordUser").getOrAwaitValue()

    Mockito.verify(storyRepository).login("Email22", "PasswordUser")
    assertNotNull(actualResponse)
    assertTrue(actualResponse is ResultResponse.Success)
    assertEquals(dummyResult, (actualResponse as ResultResponse.Success).data)
  }

  @Test
  fun `when Network Error Should Return Error`() {
    val expectedResponse = MutableLiveData<ResultResponse<LoginResult>>()
    expectedResponse.value = ResultResponse.Error("Error")
    `when`(loginViewModel.login("Email22", "PasswordUser")).thenReturn(expectedResponse)

    val actualResponse = loginViewModel.login("Email22", "PasswordUser").getOrAwaitValue()

    Mockito.verify(storyRepository).login("Email22", "PasswordUser")
    assertNotNull(actualResponse)
    assertTrue(actualResponse is ResultResponse.Error)
  }
}