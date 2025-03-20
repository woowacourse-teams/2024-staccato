package com.on.staccato.presentation.timeline.model

import androidx.annotation.MenuRes
import com.on.staccato.R

enum class SortType(
    @MenuRes val menuId: Int,
) {
    UPDATED(R.id.updated_order),
    NEWEST(R.id.newest_order),
    OLDEST(R.id.oldest_order),
    ;

    companion object {
        fun from(menuId: Int): SortType = entries.first { it.menuId == menuId }
    }
}
