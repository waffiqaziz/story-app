package com.dicoding.storyapp.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.ui.viewmodel.AddStoryViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelStoryFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AddStoryActivity : AppCompatActivity() {
  private lateinit var binding: ActivityAddStoryBinding

  private lateinit var user: UserModel
  private var getFile: File? = null
  private var result: Bitmap? = null

  private val viewModel: AddStoryViewModel by viewModels {
    ViewModelStoryFactory.getInstance(this)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE_PERMISSIONS) {
      if (!allPermissionsGranted()) {
        Helper.showToastLong(this, getString(R.string.invalid_permission))
        finish()
      }
    }
  }

  private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAddStoryBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupToolbar()

    user = intent.getParcelableExtra(EXTRA_USER)!!

    getPermission()

    binding.btnCameraX.setOnClickListener { startCameraX() }
    binding.btnGallery.setOnClickListener { startGallery() }
    binding.btnUpload.setOnClickListener { uploadImage() }
  }

  private fun getPermission() {
    if (!allPermissionsGranted()) {
      ActivityCompat.requestPermissions(
        this,
        REQUIRED_PERMISSIONS,
        REQUEST_CODE_PERMISSIONS
      )
    }
  }

  private fun setupToolbar() {
    setSupportActionBar(binding.myToolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    finish()
    return true
  }

  // cameraX
  private fun startCameraX() {
    launcherIntentCameraX.launch(Intent(this, CameraActivity::class.java))
  }

  private val launcherIntentCameraX = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    if (it.resultCode == CAMERA_X_RESULT) {
      val myFile = it.data?.getSerializableExtra("picture") as File
      val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

      getFile = myFile
      result =
        Helper.rotateBitmap(
          BitmapFactory.decodeFile(getFile?.path),
          isBackCamera
        )
    }
    binding.ivPreview.setImageBitmap(result)
  }

  private fun startGallery() {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "image/*"
    val chooser = Intent.createChooser(intent, "Choose a Picture")
    launcherIntentGallery.launch(chooser)
  }

  private val launcherIntentGallery = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    if (it.resultCode == RESULT_OK) {
      val selectedImg: Uri = it.data?.data as Uri
      val myFile = Helper.uriToFile(selectedImg, this@AddStoryActivity)
      getFile = myFile
      binding.ivPreview.setImageURI(selectedImg)
    }
  }

  private fun uploadImage() {
    when {
      binding.etDescription.text.toString().isEmpty() -> {
        binding.etDescription.error = getString(R.string.invalid_description)
      }
      getFile != null -> {
        val file = Helper.reduceFileImage(getFile as File)
        val description = binding.etDescription.text.toString()
          .toRequestBody("application/json;charset=utf-8".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
          "photo",
          file.name,
          requestImageFile
        )

        // upload story
        viewModel.postStory(user.token, description, imageMultipart).observe(this) {
          if (it != null) {
            when (it) {
              is ResultResponse.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
              }
              is ResultResponse.Success -> {
                binding.progressBar.visibility = View.GONE
                Helper.showToastLong(this, getString(R.string.upload_success))
                finish()
              }
              is ResultResponse.Error -> {
                binding.progressBar.visibility = View.GONE
                AlertDialog.Builder(this).apply {
                  setTitle(getString(R.string.information))
                  setMessage(getString(R.string.upload_failed) + ", ${it.error}")
                  setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    binding.progressBar.visibility = View.GONE
                  }
                  create()
                  show()
                }
              }
            }
          }
        }
      }
      else -> {
        Helper.showToastShort(this@AddStoryActivity, getString(R.string.no_attach_file))
      }
    }
  }

  companion object {
    const val CAMERA_X_RESULT = 200

    const val EXTRA_USER = "user"

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private const val REQUEST_CODE_PERMISSIONS = 10
  }

}