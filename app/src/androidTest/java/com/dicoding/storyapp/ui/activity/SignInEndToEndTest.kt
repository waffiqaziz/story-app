package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.test.core.app.ApplicationProvider
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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.dicoding.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInEndToEndTest {

  private val context: Context = ApplicationProvider.getApplicationContext()

  @get:Rule
  val disableAnimationsRule = DisableAnimationsRule()

  @get:Rule
  val activityScenarioRule = ActivityScenarioRule(SignInActivity::class.java)

  @Before
  fun setup() {
    Intents.init()
    IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
  }

  @After
  fun teardown() {
    Intents.release()
    IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
  }

  @Test
  fun loginTest() {
    activityScenarioRule.scenario.onActivity { activity ->
      Log.d("TEST", "Activity is ready")
    }

    onView(withId(imageView)).check(matches(isDisplayed()))
    onView(withId(ed_login_email)).check(matches(isDisplayed()))
    onView(withId(ed_login_password)).check(matches(isDisplayed()))
    onView(withId(btn_signIn)).check(matches(isDisplayed()))
    onView(withId(btn_open_register)).check(matches(isDisplayed()))

    // login
    onView(withId(ed_login_email)).perform(typeText("aaaa3@gmail.com"))
    onView(withId(ed_login_password)).perform(typeText("aaaa3@gmail.com"))
    Espresso.closeSoftKeyboard()
    onView(withId(btn_signIn)).perform(click())

    onView(withText(getString(context, continue_))).check(matches(isDisplayed())).perform(click())

    onView(withId(imageView)).check(matches(isDisplayed()))
    onView(withId(nameTextView)).check(matches(isDisplayed()))
    onView(withId(messageTextView)).check(matches(isDisplayed()))
    onView(withId(btn_lis_story)).check(matches(isDisplayed()))
    onView(withId(btn_logOut)).check(matches(isDisplayed()))

    // logout
    onView(withId(btn_logOut)).check(matches(isDisplayed())).perform(click())
    onView(withText(getString(context, continue_))).check(matches(isDisplayed())).perform(click())
    onView(withId(ed_login_email)).check(matches(isDisplayed()))
    onView(withId(ed_login_password)).check(matches(isDisplayed()))
    onView(withId(btn_signIn)).check(matches(isDisplayed()))
    onView(withId(btn_open_register)).check(matches(isDisplayed()))
  }
}
