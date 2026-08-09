package com.dicoding.storyapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId

object TestUtils {

  fun waitFor(delay: Long): ViewAction = object : ViewAction {
    override fun getConstraints() = isRoot()
    override fun getDescription() = "Wait for $delay milliseconds."
    override fun perform(uiController: UiController, view: View?) {
      uiController.loopMainThreadForAtLeast(delay)
    }
  }

  fun waitForRecyclerViewItems(viewId: Int, timeoutMs: Long = 15000) {
    val endTime = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < endTime) {
      var count = -1
      onView(withId(viewId)).check { view, _ ->
        count = (view as RecyclerView).adapter?.itemCount ?: -1
      }
      if (count > 0) return
      Thread.sleep(300)
    }
    throw AssertionError("RecyclerView still empty after ${timeoutMs}ms")
  }
}
