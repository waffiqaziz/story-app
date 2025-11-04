package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getString
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.storyapp.R.id.btn_lis_story
import com.dicoding.storyapp.R.id.btn_logOut
import com.dicoding.storyapp.R.id.btn_open_register
import com.dicoding.storyapp.R.id.btn_signIn
import com.dicoding.storyapp.R.id.ed_login_email
import com.dicoding.storyapp.R.id.ed_login_password
import com.dicoding.storyapp.R.id.imageView
import com.dicoding.storyapp.R.id.messageTextView
import com.dicoding.storyapp.R.id.nameTextView
import com.dicoding.storyapp.R.string.continue_
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class SignInEndToEndTest {

  private val context: Context = ApplicationProvider.getApplicationContext()
  private lateinit var scenario: ActivityScenario<SignInActivity>
  private val productionUrl = "https://story-api.dicoding.dev/v1/"

  @get:Rule
  val disableAnimationsRule = DisableAnimationsRule()

  @Before
  fun setup() {
    ApiConfig.BASE_URL = productionUrl
    ViewModelFactory.clearInstance()
    Intents.init()
    IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
  }

  @After
  fun teardown() {
    Intents.release()
    IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    if (::scenario.isInitialized) {
      scenario.close()
    }

    ApiConfig.BASE_URL = productionUrl
    ViewModelFactory.clearInstance()
  }

  @Test
  fun loginTest() {
    println("LOG: TEST STARTED")
    val intent = Intent(context, SignInActivity::class.java)
    scenario = launchActivity(intent)
    scenario.onActivity {}
    println("LOG: LAUNCH ACTIVITY")

    onView(withId(imageView)).check(matches(isDisplayed()))
    onView(withId(ed_login_email)).check(matches(isDisplayed()))
    onView(withId(ed_login_password)).check(matches(isDisplayed()))
    onView(withId(btn_signIn)).check(matches(isDisplayed()))
    onView(withId(btn_open_register)).check(matches(isDisplayed()))
    println("LOG: ASSERT SIGN IN VIEWS")

    // login
    onView(withId(ed_login_email)).perform(typeText("aaaa3@gmail.com"))
    onView(withId(ed_login_password)).perform(typeText("aaaa3@gmail.com"))
    Espresso.closeSoftKeyboard()
    onView(withId(btn_signIn)).perform(click())
    Thread.sleep(500)
    println("LOG: BUTTON LOGIN CLICKED")

    onView(withText(getString(context, continue_))).check(matches(isDisplayed())).perform(click())
    Thread.sleep(500)
    println("LOG: BUTTON CONTINUE CLICKED")

    onView(withId(imageView)).check(matches(isDisplayed()))
    onView(withId(nameTextView)).check(matches(isDisplayed()))
    onView(withId(messageTextView)).check(matches(isDisplayed()))
    onView(withId(btn_lis_story)).check(matches(isDisplayed()))
    onView(withId(btn_logOut)).check(matches(isDisplayed()))
    println("LOG: ASSERT MAIN VIEWS")

    // logout
    onView(withId(btn_logOut)).check(matches(isDisplayed())).perform(click())
    onView(withText(getString(context, continue_))).check(matches(isDisplayed())).perform(click())
    Thread.sleep(500)
    println("LOG: BUTTON LOG OUT CLICKED")

    onView(withId(ed_login_email)).check(matches(isDisplayed()))
    onView(withId(ed_login_password)).check(matches(isDisplayed()))
    onView(withId(btn_signIn)).check(matches(isDisplayed()))
    onView(withId(btn_open_register)).check(matches(isDisplayed()))
    println("LOG: ASSERT SIGN IN VIEWS 2")
  }
}
