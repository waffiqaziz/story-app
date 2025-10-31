package com.dicoding.storyapp.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.dicoding.storyapp.R.id.stack_view

object WidgetUpdateHelper {

  fun updateStackWidget(context: Context) {
    val intent = Intent(context, StackWidget::class.java).apply {
      action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }

    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = ComponentName(context, StackWidget::class.java)
    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
    context.sendBroadcast(intent)

    @Suppress("DEPRECATION") // safe to use, due to no others options available
    appWidgetIds.forEach { appWidgetId ->
      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, stack_view)
    }
  }
}