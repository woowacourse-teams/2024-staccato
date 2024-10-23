package com.on.staccato.presentation.timeline.model

import androidx.annotation.MenuRes
import com.on.staccato.R

enum class SortType(
    @MenuRes val menuId: Int,
) {
    CREATION(R.id.creation_order),
    LATEST(R.id.latest_order),
    OLDEST(R.id.oldest_order),
    WITH_PERIOD(R.id.with_period_order),
    WITHOUT_PERIOD(R.id.without_period_order),
}
