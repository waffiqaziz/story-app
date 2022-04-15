package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ui.viewmodel.MainViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class RoutingActivity : AppCompatActivity() {

  private lateinit var mainViewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_routing)

    setupViewModel()
  }

  private fun setupViewModel() {
    mainViewModel = ViewModelProvider(
      this,
      ViewModelFactory(UserPreference.getInstance(dataStore))
    )[MainViewModel::class.java]

    mainViewModel.getUser().observe(this) {
      if (it.isLogin) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
      }else{
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
      }
    }
  }
}