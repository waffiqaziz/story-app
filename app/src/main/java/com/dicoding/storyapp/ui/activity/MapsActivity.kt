package com.dicoding.storyapp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R.id.map
import com.dicoding.storyapp.R.raw.my_map_style
import com.dicoding.storyapp.R.string.error_occurred
import com.dicoding.storyapp.data.ResultResponse
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.helper.Helper.showToastShort
import com.dicoding.storyapp.ui.viewmodel.MapsViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.utils.Helpers.parcelable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var mMap: GoogleMap
  private lateinit var binding: ActivityMapsBinding
  private lateinit var user: UserModel

  private val viewModel: MapsViewModel by viewModels {
    ViewModelFactory.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMapsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    getParcelable()

    val mapFragment = supportFragmentManager
      .findFragmentById(map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }

  private fun getParcelable() {
    user = requireNotNull(intent.parcelable(EXTRA_USER)) {
      showToastShort(this, "Something went wrong")
      Log.e(TAG, "Data Extra is Null")
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    mMap.setMapStyle(
      MapStyleOptions.loadRawResourceStyle(
        this, my_map_style
      )
    )

    mMap.uiSettings.isZoomControlsEnabled = true
    mMap.uiSettings.isIndoorLevelPickerEnabled = true
    mMap.uiSettings.isCompassEnabled = true
    mMap.uiSettings.isMapToolbarEnabled = true

    showData()
    moveCameraToSelectedRegion()
    getMyLocation()
  }

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      getMyLocation()
    }
  }

  @SuppressLint("MissingPermission")
  private fun getMyLocation() {
    if (ContextCompat.checkSelfPermission(
        this.applicationContext,
        Manifest.permission.ACCESS_FINE_LOCATION,
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      mMap.isMyLocationEnabled = true
    } else {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  private fun showData() {
    // val boundsBuilder = LatLngBounds.Builder()
    viewModel.getStories(user.token).observe(this) {
      if (it != null) {
        when (it) {
          is ResultResponse.Loading -> {
            binding.progressBar.visibility = View.VISIBLE
          }

          is ResultResponse.Success -> {
            binding.progressBar.visibility = View.GONE
            it.data.forEachIndexed { _, element ->
              val lastLatLng = LatLng(element.lat, element.lon)

              mMap.addMarker(
                MarkerOptions().position(lastLatLng)
                  .title(element.name + "\n" + element.description)
              )
              // disable to prevent camera movement based on data
              // boundsBuilder.include(lastLatLng)
              // val bounds: LatLngBounds = boundsBuilder.build()
              // mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
            }
          }

          is ResultResponse.Error -> {
            binding.progressBar.visibility = View.GONE
            showToastShort(this, getString(error_occurred))
          }
        }
      }
    }
  }

  private fun moveCameraToSelectedRegion() {
    val selectedRegion = LatLng(DEFAULT_LAT, DEFAULT_LONG) // Indonesian coordinates

    // Set zoom level (1-21,  1 is world and 21 is street level)
    val cameraPosition = CameraPosition.Builder()
      .target(selectedRegion)
      .zoom(ZOOM_LEVEL)
      .build()

    // Move or animate the camera to the selected region
    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
  }

  companion object {
    const val TAG = "MapsActivity"
    const val EXTRA_USER = "user"
    const val ZOOM_LEVEL = 4f
    const val DEFAULT_LAT = -5.053256
    const val DEFAULT_LONG = 114.955829
  }
}