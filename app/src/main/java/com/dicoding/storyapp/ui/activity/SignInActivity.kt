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
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivitySigninBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.ui.viewmodel.*

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
        binding.etPass.text.toString().length >= 6 &&
        Helper.isEmailValid(binding.etEmail.text.toString())
  }

  private fun showAlertDialog(param: Boolean, message: String) {
    if (param) {
      AlertDialog.Builder(this).apply {
        setTitle(getString(R.string.information))
        setMessage(getString(R.string.sign_in_success))
        setPositiveButton(getString(R.string.continue_)) { _, _ ->
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
        setTitle(getString(R.string.information))
        binding.etPass.error = null
        setMessage(getString(R.string.sign_in_failed) + ", $message")
        setPositiveButton(getString(R.string.continue_)) { _, _ ->
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
          }
          is ResultResponse.Success -> {
            binding.progressBar.visibility = View.GONE
            val user = UserModel(
              it.data.name,
              email,
              pass,
              it.data.userId,
              it.data.token,
              true
            )
            showAlertDialog(true, getString(R.string.sign_in_success))

            val userPref = UserPreference.getInstance(dataStore)
            lifecycleScope.launchWhenStarted {
               userPref.saveUser(user)
            }
          }
          is ResultResponse.Error -> {
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