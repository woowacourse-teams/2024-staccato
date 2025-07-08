package com.on.staccato.presentation.category

import android.view.View

class DebouncedClick(
    private val intervalTime: Long,
    private val action: (View?) -> Unit,
) : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(view: View?) {
        val systemTime = System.currentTimeMillis()
        if (systemTime - lastClickTime > intervalTime) {
            lastClickTime = systemTime
            action.invoke(view)
        }
    }
}

fun View.setDebounceClickListener(
    interval: Long,
    action: (View?) -> Unit,
) {
    setOnClickListener(DebouncedClick(interval, action))
}
