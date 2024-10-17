package com.dicoding.storyapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.storyapp.R.string.continue_
import com.dicoding.storyapp.R.string.log_out_success
import com.dicoding.storyapp.R.string.no
import com.dicoding.storyapp.R.string.warning
import com.dicoding.storyapp.R.string.warning_log_out
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.helper.Helper.showToastShort
import com.dicoding.storyapp.ui.activity.ListStoryActivity
import com.dicoding.storyapp.ui.activity.SignInActivity
import com.dicoding.storyapp.ui.viewmodel.MainViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelUserFactory
import com.dicoding.storyapp.utils.Helpers.transparentStatusBar
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class MainActivity : AppCompatActivity() {
  private lateinit var user: UserModel
  private lateinit var mainViewModel: MainViewModel
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    transparentStatusBar(window)
    setupViewModel()
    playAnimation()
    buttonListener()
  }

  private fun setupViewModel() {
    mainViewModel = ViewModelProvider(
      this,
      ViewModelUserFactory(UserPreference.getInstance(dataStore))
    )[MainViewModel::class.java]

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        mainViewModel.getUser().collect {
          user = UserModel(
            it.name,
            it.email,
            it.password,
            it.userId,
            it.token,
            true
          )
          binding.nameTextView.text = getString(R.string.greeting, user.name)
        }
      }
    }
  }

  private fun playAnimation() {
    ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -38f, 38f).apply {
      duration = 6000
      repeatCount = ObjectAnimator.INFINITE
      repeatMode = ObjectAnimator.REVERSE
    }.start()

    val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
    val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
    val logout = ObjectAnimator.ofFloat(binding.btnLogOut, View.ALPHA, 1f).setDuration(500)

    AnimatorSet().apply {
      playSequentially(name, message, logout)
      startDelay = 500
    }.start()
  }

  private fun buttonListener() {
    binding.btnLisStory.setOnClickListener {
      val moveToListStoryActivity = Intent(this@MainActivity, ListStoryActivity::class.java)
      moveToListStoryActivity.putExtra(ListStoryActivity.EXTRA_USER, user)
      startActivity(moveToListStoryActivity)
    }
    binding.ivSetting?.setOnClickListener {
      startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
    binding.btnLogOut?.setOnClickListener {
      mainViewModel.logout()
      AlertDialog.Builder(this).apply {
        setTitle(getString(warning))
        setMessage(getString(warning_log_out))
        setPositiveButton(getString(continue_)) { dialog, _ ->
          startActivity(Intent(this@MainActivity, SignInActivity::class.java))
          dialog.dismiss()
          showToastShort(this@MainActivity, getString(log_out_success))
          finish()
        }
        setNegativeButton(getString(no)) { dialog, _ ->
          dialog.dismiss()
        }
        create()
        show()
      }
    }
  }
}