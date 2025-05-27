package com.on.staccato.presentation.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.on.staccato.R
import com.on.staccato.presentation.util.dpToPx

class MainFabBehavior(
    context: Context,
    attrs: AttributeSet,
) : FloatingActionButton.Behavior(context, attrs) {
    private var halfExpandedBottomSheetY: Float? = null
    private var initialFabY: Float? = null
    private var isFabVisible = true

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View,
    ): Boolean {
        if (dependency.id != R.id.constraint_main_bottom_sheet) return false

        if (halfExpandedBottomSheetY == null) {
            halfExpandedBottomSheetY = dependency.y
            initialFabY = dependency.y - FAB_BOTTOM_MARGIN.dpToPx(child.context)
        }

        val currentSheetY = dependency.y
        val fabY = halfExpandedBottomSheetY!! - FAB_BOTTOM_MARGIN.dpToPx(child.context)
        val isExpended = currentSheetY < halfExpandedBottomSheetY!!

        if (isExpended) {
            if (isFabVisible) {
                child.hide()
                isFabVisible = false
            }
        } else {
            if (!isFabVisible) {
                child.show()
                isFabVisible = true
            }
            child.y = fabY + currentSheetY - halfExpandedBottomSheetY!!
        }

        return true
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View,
    ): Boolean {
        return dependency.id == R.id.constraint_main_bottom_sheet
    }

    companion object {
        const val FAB_BOTTOM_MARGIN = 55f
    }
}
