package com.dicoding.storyapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.storyapp.R.string.continue_
import com.dicoding.storyapp.R.string.log_out_success
import com.dicoding.storyapp.R.string.no
import com.dicoding.storyapp.R.string.warning
import com.dicoding.storyapp.R.string.warning_log_out
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.helper.Helper.showToastShort
import com.dicoding.storyapp.ui.activity.ListStoryActivity
import com.dicoding.storyapp.ui.activity.SignInActivity
import com.dicoding.storyapp.ui.viewmodel.MainViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

  private lateinit var user: UserModel
  private lateinit var binding: ActivityMainBinding

  private val viewModel: MainViewModel by viewModels {
    ViewModelFactory.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupViewModel()
    playAnimation()
    buttonListener()
  }

  private fun setupViewModel() {
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.getUser().collect {
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
      viewModel.logout()
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