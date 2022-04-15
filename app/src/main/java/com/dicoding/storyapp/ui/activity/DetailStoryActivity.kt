package com.dicoding.storyapp.ui.activity

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.ui.viewmodel.DetailStoryViewModel
import java.util.*

class DetailStoryActivity : AppCompatActivity() {
  private lateinit var story: ListStoryItem
  private lateinit var binding: ActivityDetailStoryBinding

  private val vm: DetailStoryViewModel by viewModels()

  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDetailStoryBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // set text view description to justify
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      run {
        binding.tvDescription.justificationMode = JUSTIFICATION_MODE_INTER_WORD
      }
    }

    story = intent.getParcelableExtra(EXTRA_STORY)!!
    vm.setDetailStory(story)
    displayResult()
    setupToolbar()
  }

  private fun setupToolbar(){
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun displayResult() {
    with(binding){
      tvName.text = vm.storyItem.name
      tvCreatedTime.text = getString(R.string.created_add, Helper.formatDate(vm.storyItem.createdAt,
        TimeZone.getDefault().id ))
      tvDescription.text = vm.storyItem.description

      Glide.with(ivStory)
        .load(vm.storyItem.photoUrl) // URL Avatar
        .placeholder(R.drawable.ic_place_holder)
        .error(R.drawable.ic_broken_image)
        .into(ivStory)
    }
  }

  companion object {
    const val EXTRA_STORY = "story"
  }
}