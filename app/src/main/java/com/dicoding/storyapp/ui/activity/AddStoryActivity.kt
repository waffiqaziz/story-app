package com.dicoding.storyapp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R.string.invalid_permission
import com.dicoding.storyapp.R.string.invalid_description
import com.dicoding.storyapp.R.string.upload_success
import com.dicoding.storyapp.R.string.information
import com.dicoding.storyapp.R.string.upload_failed
import com.dicoding.storyapp.R.string.continue_
import com.dicoding.storyapp.R.string.no_attach_file
import com.dicoding.storyapp.R.string.enable_gps_permission
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.helper.Helper.showToastShort
import com.dicoding.storyapp.ui.viewmodel.AddStoryViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.utils.Helpers.parcelable
import com.dicoding.storyapp.utils.Helpers.serializable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
  private lateinit var binding: ActivityAddStoryBinding

  private lateinit var user: UserModel
  private var getFile: File? = null
  private var result: Bitmap? = null
  private var location: Location? = null
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private val viewModel: AddStoryViewModel by viewModels {
    ViewModelFactory.getInstance(this)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE_PERMISSIONS) {
      if (!allPermissionsGranted()) {
        Helper.showToastLong(this, getString(invalid_permission))
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

    getParcelable()
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    getPermission()
    buttonListener()
  }

  private fun getParcelable() {
    user = requireNotNull(intent.parcelable(EXTRA_USER)) {
      showToastShort(this, "Something went wrong")
      Log.e(TAG, "Data Extra is Null")
    }
  }

  private fun buttonListener() {
    binding.btnCameraX.setOnClickListener { startCameraX() }
    binding.btnGallery.setOnClickListener { startGallery() }
    binding.btnUpload.setOnClickListener { uploadStory() }
    binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        getMyLocation()
      } else {
        location = null
      }
    }
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
    onBackPressedDispatcher.onBackPressed()
    finish()
    return true
  }

  // cameraX
  private fun startCameraX() {
    launcherIntentCameraX.launch(Intent(this, CameraActivity::class.java))
  }

  private val launcherIntentCameraX = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == CAMERA_X_RESULT) {
      // Use the serializable extension function to retrieve the File object from the intent's data
      val myFile: File? = result.data?.serializable("picture")

      // Retrieve the boolean flag indicating if it's from the back camera
      val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) ?: true

      // Store the file and apply necessary operations (like rotating the bitmap)
      getFile = myFile
      getFile?.let { file ->
        // Decode and rotate the bitmap if the file exists
        this.result = Helper.rotateBitmap(
          BitmapFactory.decodeFile(file.path),
          isBackCamera
        )
        // Update the ImageView with the rotated bitmap
        binding.ivPreview.setImageBitmap(this.result)
      }
    }
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

  private fun uploadStory() {
    when {
      binding.etDescription.text.toString().isEmpty() -> {
        binding.etDescription.error = getString(invalid_description)
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

        var lat: RequestBody? = null
        var lon: RequestBody? = null
        if (location != null) {
          lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
          lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
        }

        // upload story
        viewModel.postStory(user.token, description, imageMultipart, lat, lon).observe(this) {
          if (it != null) {
            when (it) {
              is ResultResponse.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
              }

              is ResultResponse.Success -> {
                binding.progressBar.visibility = View.GONE
                Helper.showToastLong(this, getString(upload_success))
                finish()
              }

              is ResultResponse.Error -> {
                binding.progressBar.visibility = View.GONE
                AlertDialog.Builder(this).apply {
                  setTitle(getString(information))
                  setMessage(getString(upload_failed) + ", ${it.error}")
                  setPositiveButton(getString(continue_)) { _, _ ->
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
        showToastShort(this@AddStoryActivity, getString(no_attach_file))
      }
    }
  }

  @SuppressLint("MissingPermission")
  private fun getMyLocation() {
    if (ContextCompat.checkSelfPermission(
        this.applicationContext,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) { // Location permission granted, then set location
      fusedLocationClient.lastLocation.addOnSuccessListener {
        if (it != null) {
          location = it
          Log.d(TAG, "Lat : ${it.latitude}, Lon : ${it.longitude}")
        } else {
          Helper.showToastLong(this, getString(enable_gps_permission))
          binding.switchCompat.isChecked = false
        }
      }
    } else { // Location permission denied, then request permission
      requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }
  }

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) {
    Log.d(TAG, "$it")
    if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
      getMyLocation()
    } else binding.switchCompat.isChecked = false
  }

  companion object {
    const val CAMERA_X_RESULT = 200
    private const val TAG = "AddStoryActivity"
    const val EXTRA_USER = "user"

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private const val REQUEST_CODE_PERMISSIONS = 10
  }

}