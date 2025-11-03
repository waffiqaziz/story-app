package com.dicoding.storyapp.ui.activity

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DisableAnimationsRule : TestRule {
  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      override fun evaluate() {
        setAnimationsEnabled(false)
        try {
          base.evaluate()
        } finally {
          setAnimationsEnabled(true)
        }
      }
    }
  }

  private fun setAnimationsEnabled(enabled: Boolean) {
    val scale = if (enabled) 1.0f else 0.0f
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
      "settings put global window_animation_scale $scale"
    )
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
      "settings put global transition_animation_scale $scale"
    )
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
      "settings put global animator_duration_scale $scale"
    )
  }
}