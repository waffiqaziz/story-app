package com.dicoding.storyapp.utils

import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.R
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.Window
import java.io.Serializable

object Helpers {

  fun transparentStatusBar(window: Window) {
    if (SDK_INT >= R) {
      window.setDecorFitsSystemWindows(false)
    } else {
      // This flag is deprecated in API 30 (Android R), but necessary for older versions
      @Suppress("DEPRECATION")
      window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
    window.statusBarColor = Color.TRANSPARENT
  }

  // credits https://stackoverflow.com/a/73543350/12159309
  inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
  }

  inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
  }

  // credits https://stackoverflow.com/a/73311814/12159309
  inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
  }

  inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
  }

}