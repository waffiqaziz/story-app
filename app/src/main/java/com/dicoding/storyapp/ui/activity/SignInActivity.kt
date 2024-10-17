package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.R.string.continue_
import com.dicoding.storyapp.R.string.information
import com.dicoding.storyapp.R.string.sign_in_failed
import com.dicoding.storyapp.R.string.sign_in_success
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivitySigninBinding
import com.dicoding.storyapp.helper.Helper.isEmailValid
import com.dicoding.storyapp.ui.viewmodel.LoginViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.utils.Const.MIN_CHARACTERS
import com.dicoding.storyapp.utils.Helpers.transparentStatusBar
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SignInActivity : AppCompatActivity() {
  private lateinit var binding: ActivitySigninBinding
  private val loginViewModel: LoginViewModel by viewModels {
    ViewModelFactory.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivitySigninBinding.inflate(layoutInflater)
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    transparentStatusBar(window)
    setMyButtonEnable()
    editTextListener()
    buttonListener()
  }

  private fun editTextListener() {
    binding.etEmail.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        setMyButtonEnable()
      }

      override fun afterTextChanged(s: Editable) {
      }
    })
    binding.etPass.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        setMyButtonEnable()
      }

      override fun afterTextChanged(s: Editable) {
      }
    })

    binding.etSignIn.setOnClickListener {
      startActivity(Intent(this@SignInActivity, RegisterActivity::class.java))
      finish()
    }
  }

  private fun setMyButtonEnable() {
    val resultPass = binding.etPass.text
    val resultEmail = binding.etEmail.text

    binding.btnSignIn.isEnabled = resultPass != null && resultEmail != null &&
      binding.etPass.text.toString().length >= MIN_CHARACTERS &&
      isEmailValid(binding.etEmail.text.toString())
  }

  private fun showAlertDialog(param: Boolean, message: String) {
    if (param) {
      AlertDialog.Builder(this).apply {
        setTitle(getString(information))
        setMessage(getString(sign_in_success))
        setPositiveButton(getString(continue_)) { _, _ ->
          val intent = Intent(context, MainActivity::class.java)
          intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
          startActivity(intent)
          finish()
        }
        create()
        show()
      }
    } else {
      AlertDialog.Builder(this).apply {
        setTitle(getString(information))
        binding.etPass.error = null
        setMessage(getString(sign_in_failed) + ", $message")
        setPositiveButton(getString(continue_)) { _, _ ->
          binding.progressBar.visibility = View.GONE
        }
        create()
        show()
      }
    }
  }

  private fun buttonListener() {
    binding.btnSignIn.setOnClickListener {
      val email = binding.etEmail.text.toString()
      val pass = binding.etPass.text.toString()

      loginViewModel.login(email, pass).observe(this) {
        when (it) {
          is ResultResponse.Loading -> {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSignIn.isEnabled = false
          }

          is ResultResponse.Success -> {
            binding.btnSignIn.isEnabled = true
            binding.progressBar.visibility = View.GONE
            val user = UserModel(
              it.data.name,
              email,
              pass,
              it.data.userId,
              it.data.token,
              true
            )
            showAlertDialog(true, getString(sign_in_success))

            val userPref = UserPreference.getInstance(dataStore)
            lifecycleScope.launch {
              lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userPref.saveUser(user)
              }
            }
          }

          is ResultResponse.Error -> {
            binding.btnSignIn.isEnabled = true
            binding.progressBar.visibility = View.GONE
            showAlertDialog(false, it.error)
          }
        }
      }
    }

    binding.ivSetting?.setOnClickListener {
      startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
  }
}