package com.dicoding.storyapp.ui.activity


import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.dicoding.storyapp.JsonConverter
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ListStoryActivityTest {
  private lateinit var scenario: ActivityScenario<ListStoryActivity>
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val user = UserModel(
    name = "string",
    email = "string",
    password = "string",
    userId = "string",
    token = "string",
    true
  )
  private val mockWebServer = MockWebServer()

  @Before
  fun setUp() {
    mockWebServer.start(8080)
    ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
    IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
  }

  @After
  fun tearDown() {
    mockWebServer.shutdown()
    IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
  }

  @Test
  fun getStory_Success() {
    val intent = Intent(context, ListStoryActivity::class.java)
    intent.putExtra(ListStoryActivity.EXTRA_USER, user)
    scenario = launchActivity(intent)

    val mockResponse = MockResponse()
      .setResponseCode(200) // success
      .setBody(JsonConverter.readStringFromFile("success_response.json"))
    mockWebServer.enqueue(mockResponse)

    onView(withId(R.id.rv_story)).check(
      matches(isDisplayed())
    )
    onView(withText("Zekken"))
      .check(matches(isDisplayed()))
    onView(withId(R.id.rv_story)).perform(
      ViewActions.swipeUp()
    )
    onView(withId(R.id.rv_story))
      .perform(
        RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
          hasDescendant(withText("ya"))
        )
      )
  }

  @Test
  fun getStory_Error() {
    val intent = Intent(context, ListStoryActivity::class.java)
    intent.putExtra(ListStoryActivity.EXTRA_USER, user)
    scenario = launchActivity(intent)

    val mockResponse = MockResponse()
      .setResponseCode(500) // error
    mockWebServer.enqueue(mockResponse)

    onView(withId(R.id.rv_story))
      .check(matches(isDisplayed()))
    onView(withText("Oops.. something went wrong. Check your connection"))
      .check(matches(isDisplayed()))
  }

}