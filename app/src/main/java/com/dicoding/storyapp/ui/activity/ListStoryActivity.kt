package com.dicoding.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.ui.adapter.LoadingStateAdapter
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.ui.viewmodel.ListStoryViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelRepoFactory


class ListStoryActivity : AppCompatActivity() {

  private var _binding: ActivityListStoryBinding? = null
  private val binding get() = _binding

  private lateinit var user: UserModel
  private lateinit var adapter: StoryAdapter

  private val viewModel: ListStoryViewModel by viewModels {
    ViewModelRepoFactory.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityListStoryBinding.inflate(layoutInflater)
    setContentView(binding?.root)

    setupToolbar()
    buttonListener()

    user = intent.getParcelableExtra(EXTRA_USER)!!

    setupRecycleView()

    showRecycleView()

    getData()
  }

  private fun setupToolbar() {
    setSupportActionBar(binding?.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

  private fun getData() {

    viewModel.getStory(user.token).observe(this) {
      adapter.submitData(lifecycle, it)
    }
  }

  private fun setupRecycleView() {
    adapter = StoryAdapter()
    binding?.rvStory?.layoutManager = LinearLayoutManager(this)
    binding?.rvStory?.setHasFixedSize(true)
    binding?.rvStory?.adapter = adapter

    binding?.rvStory?.adapter = adapter.withLoadStateHeaderAndFooter(
      footer = LoadingStateAdapter(adapter::retry),
      header = LoadingStateAdapter(adapter::retry)
    )
  }

  private fun showRecycleView() {
    adapter.addLoadStateListener {
      binding?.apply {
        if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && adapter.itemCount < 1) {
          tvInfo.visibility = View.VISIBLE
          rvStory.visibility = View.VISIBLE
          progressBar.visibility = View.GONE
        } else {
          progressBar.visibility = View.GONE
          rvStory.visibility = View.VISIBLE
          tvInfo.visibility = View.GONE
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    getData()
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  private fun buttonListener() {
    binding?.ivAddStory?.setOnClickListener {
      val moveToAddStoryActivity = Intent(this, AddStoryActivity::class.java)
      moveToAddStoryActivity.putExtra(AddStoryActivity.EXTRA_USER, user)
      startActivity(moveToAddStoryActivity)
    }
    binding?.ivShowMap?.setOnClickListener {
      val moveToMapStory = Intent(this, MapsActivity::class.java)
      moveToMapStory.putExtra(AddStoryActivity.EXTRA_USER, user)
      startActivity(moveToMapStory)
    }
  }

  companion object {
    const val EXTRA_USER = "user"
  }
}