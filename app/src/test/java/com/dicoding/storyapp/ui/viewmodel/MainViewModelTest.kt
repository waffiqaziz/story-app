package com.dicoding.storyapp.ui.viewmodel

import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainCoroutineRule
import com.dicoding.storyapp.data.model.UserPreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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
class MainViewModelTest {
  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()

  @Mock
  private lateinit var userPreference: UserPreference
  private lateinit var mainViewModel: MainViewModel

  @Before
  fun setUp() {
    mainViewModel = MainViewModel(userPreference)
  }

  @Test
  fun `when getUser() is called should no null and Return Success`() = runTest {
    val expectedResponse = flowOf(DataDummy.generateDummyUserModel())
    `when`(mainViewModel.getUser()).thenReturn(expectedResponse)

    mainViewModel.getUser().collect {
      Assert.assertNotNull(it.token)
      Assert.assertEquals(DataDummy.generateDummyUserModel().token, it.token)
    }

    Mockito.verify(userPreference).getUser()
  }

  @Test
  fun `when logOut() is called should Success`() = runTest {
    mainViewModel.logout()
    Mockito.verify(userPreference).logout()
  }

}