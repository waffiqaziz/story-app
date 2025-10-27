package com.dicoding.storyapp.utils

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Parcelable
import java.io.Serializable

object Helpers {

  inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
  }

  // credits https://stackoverflow.com/a/73311814/12159309
  inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
  }
}
