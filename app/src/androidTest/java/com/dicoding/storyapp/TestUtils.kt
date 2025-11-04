package com.dicoding.storyapp

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot

object TestUtils {

  fun waitFor(delay: Long): ViewAction = object : ViewAction {
    override fun getConstraints() = isRoot()
    override fun getDescription() = "Wait for $delay milliseconds."
    override fun perform(uiController: UiController, view: View?) {
      uiController.loopMainThreadForAtLeast(delay)
    }
  }
}
