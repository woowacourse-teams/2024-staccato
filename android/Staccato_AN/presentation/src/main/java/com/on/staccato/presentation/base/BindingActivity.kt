package com.on.staccato.presentation.base

import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BindingActivity<T : ViewDataBinding> : AppCompatActivity() {
    abstract val layoutResourceId: Int
    private var _binding: T? = null
    val binding
        get() = requireNotNull(_binding)
    private val gestureDetector by lazy { GestureDetector(this, SingleTapListener()) }
    protected val inputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private var prevFocus: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutResourceId)
        initStartView(savedInstanceState)
    }

    abstract fun initStartView(savedInstanceState: Bundle?)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            prevFocus = currentFocus
        }
        val dispatchedTouchEventResult = super.dispatchTouchEvent(motionEvent)
        gestureDetector.onTouchEvent(motionEvent)
        return dispatchedTouchEventResult
    }

    private fun hideKeyboardAndClearFocus(focusedView: View) {
        inputMethodManager.hideSoftInputFromWindow(
            focusedView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS,
        )
        focusedView.clearFocus()
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
            if (motionEvent.action == MotionEvent.ACTION_UP && prevFocus is EditText) {
                val prevFocus = prevFocus ?: return false
                val hitRect = Rect()
                prevFocus.getGlobalVisibleRect(hitRect)

                if (didTouchOutsideFocusedView(hitRect, motionEvent)) {
                    if (currentFocus is EditText && currentFocus != prevFocus) {
                        return false
                    } else {
                        hideKeyboardAndClearFocus(focusedView = prevFocus)
                    }
                }
            }
            return super.onSingleTapUp(motionEvent)
        }

        private fun didTouchOutsideFocusedView(
            hitRect: Rect,
            motionEvent: MotionEvent,
        ) = !hitRect.contains(motionEvent.x.toInt(), motionEvent.y.toInt())
    }
}
