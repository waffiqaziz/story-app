package com.dicoding.storyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.ui.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var mMap: GoogleMap
  private lateinit var binding: ActivityMapsBinding
  private lateinit var user: UserModel

  private val viewModel by viewModels<MapsViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMapsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    user = intent.getParcelableExtra(EXTRA_USER)!!

    val mapFragment = supportFragmentManager
      .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)

    showSnackBar()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    mMap.setMapStyle(
      MapStyleOptions.loadRawResourceStyle(
        this, R.raw.map_style
      )
    )

    mMap.uiSettings.isZoomControlsEnabled = true
    mMap.uiSettings.isIndoorLevelPickerEnabled = true
    mMap.uiSettings.isCompassEnabled = true
    mMap.uiSettings.isMapToolbarEnabled = true

    // Add a marker in Sydney and move the camera
    val monasIndonesia = LatLng(-6.17555357, 106.82721585)
    mMap.addMarker(MarkerOptions().position(monasIndonesia).title("Marker in Sydney"))

    val a = LatLng(-6.17545357, 106.82731585)
    mMap.addMarker(MarkerOptions().position(a).title("Marker in Sydney"))
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(a, 15f))

    getMyLocation()
  }

  private val requestPermissionLauncher =
    registerForActivityResult(
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
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      mMap.isMyLocationEnabled = true
    } else {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }


  private fun showSnackBar() {
    viewModel.snackBarText.observe(this) {
      it.getContentIfNotHandled()?.let { snackBarText ->
        Snackbar.make(
          findViewById(R.id.rv_story),
          snackBarText,
          Snackbar.LENGTH_SHORT
        ).show()
      }
    }
//    viewModel.isHaveData.observe(this){
//      binding.apply {
//        if (it) {
//          rvStory.visibility = View.VISIBLE
//          tvInfo.visibility = View.GONE
//        } else {
//          rvStory.visibility = View.GONE
//          tvInfo.visibility = View.VISIBLE
//        }
//      }
//    }
  }

  companion object {
    const val EXTRA_USER = "user"
  }
}