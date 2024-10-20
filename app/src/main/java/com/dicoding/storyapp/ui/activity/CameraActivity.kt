package com.dicoding.storyapp.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R.string.failed_open_camera
import com.dicoding.storyapp.R.string.invalid_capture
import com.dicoding.storyapp.databinding.ActivityCameraBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.ui.activity.AddStoryActivity.Companion.CAMERA_X_RESULT

class CameraActivity : AppCompatActivity() {
  private lateinit var binding: ActivityCameraBinding
  private var imageCapture: ImageCapture? = null
  private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityCameraBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.captureImage.setOnClickListener { takePhoto() }
    binding.switchCamera.setOnClickListener {
      cameraSelector =
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
        else CameraSelector.DEFAULT_BACK_CAMERA
      startCamera()
    }
  }

  public override fun onResume() {
    super.onResume()
    hideSystemUI()
    startCamera()
  }

  private fun takePhoto() {
    val imageCapture = imageCapture ?: return

    val photoFile = Helper.createFile(application)

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(
      outputOptions,
      ContextCompat.getMainExecutor(this),
      object : ImageCapture.OnImageSavedCallback {
        override fun onError(exc: ImageCaptureException) {
          Helper.showToastShort(this@CameraActivity, getString(invalid_capture))
        }

        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
          val intent = Intent()
          intent.putExtra("picture", photoFile)
          intent.putExtra(
            "isBackCamera",
            cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
          )
          setResult(CAMERA_X_RESULT, intent)
          finish()
        }
      }
    )
  }

  private fun startCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
      val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
      val preview = Preview.Builder()
        .build()
        .also {
          it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        }

      imageCapture = ImageCapture.Builder().build()

      try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
          this,
          cameraSelector,
          preview,
          imageCapture
        )
      } catch (exc: Exception) {
        Helper.showToastShort(this, getString(failed_open_camera))
      }
    }, ContextCompat.getMainExecutor(this))
  }

  private fun hideSystemUI() {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
      window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
      )
    }
    supportActionBar?.hide()
  }
}