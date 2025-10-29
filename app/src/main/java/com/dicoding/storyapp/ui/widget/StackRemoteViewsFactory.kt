package com.dicoding.storyapp.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.room.StoryDao
import com.dicoding.storyapp.data.room.StoryDatabase
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(private val mContext: Context) :
  RemoteViewsService.RemoteViewsFactory {

  private var mWidgetItems = listOf<ListStoryItem>()

  private lateinit var dao: StoryDao

  override fun getCount(): Int = mWidgetItems.size

  override fun getItemId(i: Int): Long = 0

  override fun getLoadingView(): RemoteViews? = null

  override fun getViewAt(position: Int): RemoteViews {
    val rv = RemoteViews(mContext.packageName, R.layout.item_widget)
    try {
      val bitmap: Bitmap = Glide.with(mContext.applicationContext)
        .asBitmap()
        .load(mWidgetItems[position].photoUrl)
        .override(600, 400)
        .fitCenter()
        .timeout(5000)
        .submit()
        .get()
      rv.setImageViewBitmap(R.id.imageView, bitmap)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return rv
  }

  override fun getViewTypeCount(): Int = 1

  override fun hasStableIds(): Boolean = false

  override fun onCreate() {
    dao = StoryDatabase.getInstance(mContext.applicationContext).storyDao()
    fetchDataDB()
  }

  override fun onDataSetChanged() {
    fetchDataDB()
  }

  override fun onDestroy() {  }

  private fun fetchDataDB() {
    runBlocking {
      mWidgetItems = dao.getAllAsList()
    }
  }
}