package com.dicoding.storyapp.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.dicoding.storyapp.R.drawable.ic_broken_image
import com.dicoding.storyapp.R.drawable.ic_place_holder
import com.dicoding.storyapp.R.string.created_add
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.helper.Helper.justifyTextView
import com.dicoding.storyapp.ui.viewmodel.DetailStoryViewModel
import com.dicoding.storyapp.utils.Helpers.parcelable
import java.util.TimeZone

class DetailStoryActivity : AppCompatActivity() {
  private lateinit var story: ListStoryItem
  private lateinit var binding: ActivityDetailStoryBinding

  private val vm: DetailStoryViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    binding = ActivityDetailStoryBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // set text view description to justify
    justifyTextView(binding.tvDescription)

    getParcelable()
    vm.setDetailStory(story)
    displayResult()
    setupToolbar()
  }

  private fun getParcelable() {
    story = requireNotNull(intent.parcelable(EXTRA_STORY)) {
      Helper.showToastShort(this, "Something went wrong")
      Log.e(TAG, "Data Extra is Null")
    }
  }

  private fun setupToolbar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressedDispatcher.onBackPressed()
    finish()
    return true
  }

  private fun displayResult() {
    binding.apply {
      tvName.text = vm.storyItem.name
      tvCreatedTime.text = getString(
        created_add, Helper.formatDate(
          vm.storyItem.createdAt,
          TimeZone.getDefault().id
        )
      )
      tvDescription.text = vm.storyItem.description

      Glide.with(ivStory)
        .load(vm.storyItem.photoUrl) // URL Avatar
        .placeholder(ic_place_holder)
        .transition(withCrossFade())
        .error(ic_broken_image)
        .into(ivStory)
    }
  }

  companion object {
    const val TAG = "DetailStoryActivity"
    const val EXTRA_STORY = "story"
  }
}