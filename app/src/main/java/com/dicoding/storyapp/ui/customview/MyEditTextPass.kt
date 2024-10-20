package com.dicoding.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R.drawable.border_corner
import com.dicoding.storyapp.R.drawable.ic_eye
import com.dicoding.storyapp.R.drawable.ic_eye_off
import com.dicoding.storyapp.R.string.invalid_password
import com.google.android.material.textfield.TextInputEditText

class MyEditTextPass : TextInputEditText, View.OnTouchListener {

  private lateinit var eyeIcon: Drawable

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
    showEyeButton()
    setBackgroundResource(border_corner)
    textSize = 15f
    textAlignment = View.TEXT_ALIGNMENT_VIEW_START
  }

  private fun init() {
    eyeIcon = ContextCompat.getDrawable(context, ic_eye_off) as Drawable // x button

    setOnTouchListener(this)

    addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // Do nothing.
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // Do nothing.
      }

      override fun afterTextChanged(s: Editable) {
        // check input
        if (s.toString().length < 8) showError()
      }
    })
  }

  private fun showError() {
    error = context.getString(invalid_password)
  }

  private fun showEyeButton() {
    setButtonDrawables(endOfTheText = eyeIcon)
  }

  private fun hideEyeButton() {
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
      val eyeButtonStart: Float
      val eyeButtonEnd: Float
      var isEyeButtonClicked = false

      if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
        eyeButtonEnd = (eyeIcon.intrinsicWidth + paddingStart).toFloat()
        if (event.x < eyeButtonEnd) isEyeButtonClicked = true
      } else {
        eyeButtonStart = (width - paddingEnd - eyeIcon.intrinsicWidth).toFloat()
        if (event.x > eyeButtonStart) isEyeButtonClicked = true
      }

      if (isEyeButtonClicked) {
        // save last cursor position
        val mSelectionStart = selectionStart
        val mSelectionEnd = selectionEnd

        return when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            hideEyeButton()
            if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
              transformationMethod = PasswordTransformationMethod.getInstance() // hide password
              eyeIcon = ContextCompat.getDrawable(context, ic_eye_off) as Drawable
              showEyeButton()
            } else {
              transformationMethod = HideReturnsTransformationMethod.getInstance() // show password
              eyeIcon = ContextCompat.getDrawable(context, ic_eye) as Drawable
              showEyeButton()
            }
            setSelection(mSelectionStart, mSelectionEnd)
            true
          }
          else -> false
        }
      }
    }
    return false
  }
}