package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ListStoryActivityEndToEndTest {
  private val user = UserModel(
    name = "string",
    email = "string",
    password = "string",
    userId = "string",
    token = "string",
    true
  )
  private lateinit var scenario: ActivityScenario<MainActivity>
  private val context: Context = ApplicationProvider.getApplicationContext()

  @Before
  fun setUp() {
    IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
  }

  @After
  fun tearDown() {
    IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
  }

  @Test
  fun loadListStory() {
    val intent = Intent(context, ListStoryActivity::class.java)
    intent.putExtra(ListStoryActivity.EXTRA_USER, user)
    scenario = launchActivity(intent)

    Intents.init()
    onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
    onView(withId(R.id.rv_story)).perform(
      RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
        10
      )
    )
    onView(withId(R.id.swipe_refresh)).check(matches(isDisplayed()))
    onView(withId(R.id.iv_add_story)).check(matches(isDisplayed()))
    onView(withId(R.id.iv_show_map)).check(matches(isDisplayed()))
    onView(withId(R.id.rv_story)).perform(
      RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
        0,
        click()
      )
    )
    Intents.release()
  }

  @Test
  fun loadDetailStory() {
    val intent = Intent(context, ListStoryActivity::class.java)
    intent.putExtra(ListStoryActivity.EXTRA_USER, user)
    scenario = launchActivity(intent)

    Intents.init()
    onView(withId(R.id.rv_story)).perform(
      RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
        0,
        click()
      )
    )
    intended(hasComponent(DetailStoryActivity::class.java.name))
    onView(withId(R.id.detail_view)).check(matches(isDisplayed()))
    onView(withId(R.id.tv_description)).check(matches(isDisplayed()))
    onView(withId(R.id.iv_story)).check(matches(isDisplayed()))
    onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
    onView(withId(R.id.tv_created_time)).check(matches(isDisplayed()))
    Intents.release()
  }

  @Test
  fun loadStoryMap() {
    val intent = Intent(context, ListStoryActivity::class.java)
    intent.putExtra(ListStoryActivity.EXTRA_USER, user)
    scenario = launchActivity(intent)

    Intents.init()
    onView(withId(R.id.iv_show_map)).perform(click())
    intended(hasComponent(MapsActivity::class.java.name))
    onView(withId(R.id.map_view)).check(matches(isDisplayed()))
    Intents.release()
  }

  @Test
  fun loadAddStory() {
    val intent = Intent(context, ListStoryActivity::class.java)
    intent.putExtra(ListStoryActivity.EXTRA_USER, user)
    scenario = launchActivity(intent)

    onView(withId(R.id.iv_add_story)).perform(click())
    onView(withId(R.id.switchCompat)).check(matches(isDisplayed()))
    onView(withId(R.id.btn_gallery)).check(matches(isDisplayed()))
    onView(withId(R.id.btn_camera_x)).check(matches(isDisplayed()))
    onView(withId(R.id.et_description)).check(matches(isDisplayed()))
    onView(withId(R.id.btn_upload)).check(matches(isDisplayed()))
  }
}