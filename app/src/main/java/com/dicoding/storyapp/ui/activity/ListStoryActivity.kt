package com.dicoding.storyapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.ui.adapter.LoadingStateAdapter
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.ui.viewmodel.ListStoryViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.utils.Helpers.parcelable
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

    getParcelable()
    initAdapter()
    initSwipeToRefresh()
    initToolbar()
    buttonListener()
  }

  private fun getParcelable() {
    user = requireNotNull(intent.parcelable(EXTRA_USER)) {
      Helper.showToastShort(this, "Something went wrong")
      Log.e(TAG, "Data Extra is Null")
    }
  }

  private fun initAdapter() {
    adapter = StoryAdapter()
    binding?.rvStory?.adapter = adapter.withLoadStateHeaderAndFooter(
      footer = LoadingStateAdapter { adapter.retry() },
      header = LoadingStateAdapter { adapter.retry() }
    )
    binding?.rvStory?.layoutManager = LinearLayoutManager(this)
    binding?.rvStory?.setHasFixedSize(true)

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        adapter.loadStateFlow.collect {
          binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
        }
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
    onBackPressedDispatcher.onBackPressed()
    finish()
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
    const val TAG = "ListStoryActivity"
    const val EXTRA_USER = "user"
  }
}