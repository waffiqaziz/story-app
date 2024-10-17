package com.dicoding.storyapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R.drawable.ic_broken_image
import com.dicoding.storyapp.R.drawable.ic_place_holder
import com.dicoding.storyapp.R.string.created_add
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemListStoryBinding
import com.dicoding.storyapp.helper.Helper
import com.dicoding.storyapp.ui.activity.DetailStoryActivity
import java.util.TimeZone

class StoryAdapter :
  PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = getItem(position)
    if (data != null) {
      holder.bind(data)
    }
  }

  inner class ViewHolder(private var binding: ItemListStoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(story: ListStoryItem) {
      binding.apply {
        Glide.with(imgItemImage)
          .load(story.photoUrl) // URL Avatar
          .placeholder(ic_place_holder)
          .error(ic_broken_image)
          .into(imgItemImage)
        tvName.text = story.name
        tvDescription.text = story.description
        tvCreatedTime.text =
          binding.root.resources.getString(
            created_add,
            Helper.formatDate(story.createdAt, TimeZone.getDefault().id)
          )

        itemView.setOnClickListener {
          val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
              itemView.context as Activity,
              Pair(imgItemImage, "image"),
              Pair(tvName, "name"),
              Pair(tvCreatedTime, "created"),
              Pair(tvDescription, "description"),
            )

          val intent = Intent(it.context, DetailStoryActivity::class.java)
          intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
          it.context.startActivity(intent, optionsCompat.toBundle())
        }
      }
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
      override fun areItemsTheSame(
        oldItem: ListStoryItem,
        newItem: ListStoryItem
      ): Boolean {
        return oldItem == newItem
      }

      override fun areContentsTheSame(
        oldItem: ListStoryItem,
        newItem: ListStoryItem
      ): Boolean {
        return oldItem.id == newItem.id
      }
    }
  }
}