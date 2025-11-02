package com.dicoding.storyapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Description
import org.hamcrest.Matcher

object TestUtils {

  fun waitFor(delay: Long): ViewAction = object : ViewAction {
    override fun getConstraints() = isRoot()
    override fun getDescription() = "Wait for $delay milliseconds."
    override fun perform(uiController: UiController, view: View?) {
      uiController.loopMainThreadForAtLeast(delay)
    }
  }

  private fun hasMinimumChildCount(minCount: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
      override fun describeTo(description: Description) {
        description.appendText("RecyclerView should have at least $minCount items")
      }

      override fun matchesSafely(view: RecyclerView): Boolean {
        return (view.adapter?.itemCount ?: 0) >= minCount
      }
    }
  }
}
