package com.dicoding.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R.drawable.border_corner
import com.dicoding.storyapp.R.drawable.ic_close
import com.dicoding.storyapp.R.string.must_filled
import com.google.android.material.textfield.TextInputEditText

class MyEditTextName : TextInputEditText, View.OnTouchListener {

  private lateinit var clearButton: Drawable

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
    setBackgroundResource(border_corner)
    textSize = 15f
    textAlignment = View.TEXT_ALIGNMENT_VIEW_START
  }

  private fun init() {
    clearButton = ContextCompat.getDrawable(context, ic_close) as Drawable // x button

    setOnTouchListener(this)

    addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // Do nothing.
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // Do nothing.
      }

      override fun afterTextChanged(s: Editable) {
        if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()

        // check input
        if (s.toString().isEmpty()) showError()
      }
    })
  }

  private fun showError() {
    error = context.getString(must_filled)
  }

  private fun showClearButton() {
    setButtonDrawables(endOfTheText = clearButton)
  }

  private fun hideClearButton() {
    setButtonDrawables()
  }

  private fun setButtonDrawables(
    startOfTheText: Drawable? = null,
    topOfTheText: Drawable? = null,
    endOfTheText: Drawable? = null,
    bottomOfTheText: Drawable? = null
  ) {
    setCompoundDrawablesWithIntrinsicBounds(
      startOfTheText,
      topOfTheText,
      endOfTheText,
      bottomOfTheText
    )
  }

  override fun onTouch(v: View?, event: MotionEvent): Boolean {
    if (compoundDrawables[2] != null) {
      val clearButtonStart: Float
      val clearButtonEnd: Float
      var isClearButtonClicked = false

      if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
        clearButtonEnd = (clearButton.intrinsicWidth + paddingStart).toFloat()
        if (event.x < clearButtonEnd) isClearButtonClicked = true
      } else {
        clearButtonStart = (width - paddingEnd - clearButton.intrinsicWidth).toFloat()
        if (event.x > clearButtonStart) isClearButtonClicked = true
      }

      if (isClearButtonClicked) {
        return when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            clearButton = ContextCompat.getDrawable(context, ic_close) as Drawable
            showClearButton() // show button x
            true
          }

          MotionEvent.ACTION_UP -> {
            clearButton = ContextCompat.getDrawable(context, ic_close) as Drawable
            if (text != null) text?.clear() // clear text
            hideClearButton()  // hide button x
            true
          }

          else -> false
        }
      } else return false
    }
    return false
  }
}