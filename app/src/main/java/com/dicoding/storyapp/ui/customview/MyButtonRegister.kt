package com.dicoding.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R.color.black
import com.dicoding.storyapp.R.color.white
import com.dicoding.storyapp.R.drawable.bg_button_disable
import com.dicoding.storyapp.R.drawable.bg_button_regular
import com.dicoding.storyapp.R.string.fill_all
import com.dicoding.storyapp.R.string.register

class MyButtonRegister : AppCompatButton {

  private lateinit var enabledBackground: Drawable
  private lateinit var disabledBackground: Drawable
  private var txtColor: Int = 1

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  ) {
    init()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    background = if (isEnabled) enabledBackground else disabledBackground

    setTextColor(ContextCompat.getColor(context, white))
    textSize = 15f
    gravity = Gravity.CENTER
    text = if (isEnabled) context.getString(register) else context.getString(fill_all)
  }

  private fun init() {
    txtColor = ContextCompat.getColor(context, black)
    enabledBackground = ContextCompat.getDrawable(context, bg_button_regular) as Drawable
    disabledBackground = ContextCompat.getDrawable(context, bg_button_disable) as Drawable
  }
}