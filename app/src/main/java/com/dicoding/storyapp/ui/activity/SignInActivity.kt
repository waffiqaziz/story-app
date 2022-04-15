package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivitySigninBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.ui.viewmodel.SignInViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SignInActivity : AppCompatActivity() {
  private lateinit var signInViewModel: SignInViewModel
  private lateinit var binding: ActivitySigninBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivitySigninBinding.inflate(layoutInflater)
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setupViewModel()
    setMyButtonEnable()
    editTextListener()
    buttonListener()
    showLoading()
  }

  private fun setupViewModel() {
    signInViewModel = ViewModelProvider(
      this,
      ViewModelFactory(UserPreference.getInstance(dataStore))
    )[SignInViewModel::class.java]
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
        setMessage(getString(R.string.sign_in_failed) +", $message")
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

      signInViewModel.login(email, pass, object : Helper.ApiCallbackString {
        override fun onResponse(success: Boolean,message: String) {
          showAlertDialog(success, message)
        }
      })
    }

    binding.ivSetting?.setOnClickListener {
      startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
  }

  private fun showLoading() {
    signInViewModel.isLoading.observe(this) {
      binding.apply {
        if (it) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE
      }
    }
  }

}