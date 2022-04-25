package com.dicoding.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.ui.adapter.LoadingStateAdapter
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.ui.viewmodel.ListStoryViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListStoryActivity : AppCompatActivity() {

  private var _binding: ActivityListStoryBinding? = null
  private val binding get() = _binding

  private lateinit var user: UserModel
  private lateinit var adapter: StoryAdapter

  private val viewModel: ListStoryViewModel by viewModels {
    ViewModelFactory.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityListStoryBinding.inflate(layoutInflater)
    setContentView(binding?.root)
    user = intent.getParcelableExtra(EXTRA_USER)!!

    initAdapter()
    initSwipeToRefresh()
    initToolbar()
    buttonListener()
  }

  private fun initAdapter() {
    adapter = StoryAdapter()
    binding?.rvStory?.adapter = adapter.withLoadStateHeaderAndFooter(
      footer = LoadingStateAdapter { adapter.retry() },
      header = LoadingStateAdapter { adapter.retry() }
    )
    binding?.rvStory?.layoutManager = LinearLayoutManager(this)
    binding?.rvStory?.setHasFixedSize(true)

    lifecycleScope.launchWhenCreated {
      adapter.loadStateFlow.collect {
        binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
      }
    }
    lifecycleScope.launch {
      adapter.loadStateFlow.collectLatest { loadStates ->
        binding?.viewError?.root?.isVisible = loadStates.refresh is LoadState.Error
      }
      if (adapter.itemCount < 1) binding?.viewError?.root?.visibility = View.VISIBLE
      else binding?.viewError?.root?.visibility = View.GONE
    }

    viewModel.getStory(user.token).observe(this) {
      adapter.submitData(lifecycle, it)
    }
  }

  // update data when swipe
  private fun initSwipeToRefresh() {
    binding?.swipeRefresh?.setOnRefreshListener { adapter.refresh() }
  }

  private fun initToolbar() {
    setSupportActionBar(binding?.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
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