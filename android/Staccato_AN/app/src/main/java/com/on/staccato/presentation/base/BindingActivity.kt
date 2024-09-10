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
    private val mDetector by lazy { GestureDetector(this, SingleTapListener()) }
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP)
            prevFocus = currentFocus
        mDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_UP && prevFocus is EditText) {
                val prevFocus = prevFocus ?: return false
                val hitRect = Rect()
                prevFocus.getGlobalVisibleRect(hitRect)

                if (!hitRect.contains(e.x.toInt(), e.y.toInt())) {
                    if (currentFocus is EditText && currentFocus != prevFocus) {
                        return false
                    } else {
                        inputMethodManager.hideSoftInputFromWindow(
                            prevFocus.windowToken,
                            InputMethodManager.HIDE_NOT_ALWAYS,
                        )
                        prevFocus.clearFocus()
                    }
                }
            }
            return super.onSingleTapUp(e)
        }
    }
}
