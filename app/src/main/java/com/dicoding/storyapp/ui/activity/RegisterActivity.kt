package com.dicoding.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R.string.continue_
import com.dicoding.storyapp.R.string.information
import com.dicoding.storyapp.R.string.register_failed
import com.dicoding.storyapp.R.string.register_success
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.helper.Helper.isEmailValid
import com.dicoding.storyapp.ui.viewmodel.RegisterViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.utils.Const.MIN_CHARACTERS
import com.dicoding.storyapp.utils.Helpers.transparentStatusBar

class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding
  private val registerViewModel: RegisterViewModel by viewModels {
    ViewModelFactory.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivityRegisterBinding.inflate(layoutInflater)
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

    binding.etSignin.setOnClickListener {
      startActivity(Intent(this@RegisterActivity, SignInActivity::class.java))
      finish()
    }
  }

  private fun setMyButtonEnable() {
    binding.btnRegister.isEnabled =
      binding.etEmail.text.toString().isNotEmpty() &&
        binding.etPass.text.toString().isNotEmpty() &&
        binding.etPass.text.toString().length >= MIN_CHARACTERS &&
        isEmailValid(binding.etEmail.text.toString())
  }

  private fun buttonListener() {
    binding.btnRegister.setOnClickListener {
      val name = binding.etName.text.toString()
      val email = binding.etEmail.text.toString()
      val password = binding.etPass.text.toString()

      registerViewModel.register(name, email, password).observe(this) {
        when (it) {
          is ResultResponse.Loading -> {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRegister.isEnabled = false
          }

          is ResultResponse.Success -> {
            binding.progressBar.visibility = View.GONE
            showAlertDialog(true, getString(register_success))
          }

          is ResultResponse.Error -> {
            binding.btnRegister.isEnabled = true
            binding.progressBar.visibility = View.GONE
            showAlertDialog(false, it.error)
          }
        }
      }
    }
    binding.ivSetting.setOnClickListener {
      startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
  }

  private fun showAlertDialog(param: Boolean, message: String) {
    if (param) {
      AlertDialog.Builder(this).apply {
        setTitle(getString(information))
        setMessage(message)
        setPositiveButton(getString(continue_)) { _, _ ->
          val intent = Intent(context, SignInActivity::class.java)
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
        setMessage(getString(register_failed) + ", $message")
        setPositiveButton(getString(continue_)) { _, _ ->
          binding.progressBar.visibility = View.GONE
        }
        create()
        show()
      }
    }
  }
}