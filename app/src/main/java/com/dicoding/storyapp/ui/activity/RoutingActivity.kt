package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.ui.viewmodel.MainViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelUserFactory
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class RoutingActivity : AppCompatActivity() {

  private lateinit var mainViewModel: MainViewModel
  private lateinit var splashScreen: SplashScreen

  override fun onCreate(savedInstanceState: Bundle?) {
    splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    splashScreen.setKeepOnScreenCondition { true }
    setupViewModel()
  }

  private fun setupViewModel() {
    mainViewModel = ViewModelProvider(
      this,
      ViewModelUserFactory(UserPreference.getInstance(dataStore))
    )[MainViewModel::class.java]

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        mainViewModel.getUser().collect {
          if (it.isLogin) {
            gotoMainActivity(true)
          } else gotoMainActivity(false)
        }
      }
    }
  }

  private fun gotoMainActivity(boolean: Boolean) {
    if (boolean) {
      startActivity(Intent(this, MainActivity::class.java))
    } else {
      startActivity(Intent(this, SignInActivity::class.java))
    }
    finish()
  }
}