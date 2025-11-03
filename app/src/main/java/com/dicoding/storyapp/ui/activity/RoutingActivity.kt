package com.dicoding.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.ui.viewmodel.MainViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class RoutingActivity : AppCompatActivity() {

  private lateinit var splashScreen: SplashScreen

  private val viewModel: MainViewModel by viewModels {
    ViewModelFactory.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    splashScreen.setKeepOnScreenCondition { true }
    setupViewModel()
  }

  private fun setupViewModel() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.getUser().collect {
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