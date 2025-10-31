package com.dicoding.storyapp.ui.customview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import kotlin.let

class AspectRatioImageView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    drawable?.let {
      val width = measuredWidth
      val height = (width * it.intrinsicHeight / it.intrinsicWidth)
      setMeasuredDimension(width, height)
    }
  }
}
