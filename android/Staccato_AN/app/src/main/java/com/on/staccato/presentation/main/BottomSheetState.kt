package com.on.staccato.presentation.main

import com.google.android.material.bottomsheet.BottomSheetBehavior

enum class BottomSheetState {
    EXPANDED,
    HALF_EXPANDED,
    COLLAPSED,
    NONE,
    ;

    companion object {
        fun Int.toBottomSheetState(): BottomSheetState {
            return when (this) {
                BottomSheetBehavior.STATE_EXPANDED -> EXPANDED
                BottomSheetBehavior.STATE_HALF_EXPANDED -> HALF_EXPANDED
                BottomSheetBehavior.STATE_COLLAPSED -> COLLAPSED
                else -> NONE
            }
        }
    }
}
