package com.on.staccato.presentation.timeline.adapter

enum class TimelineViewType(val viewType: Int) {
    FIRST_ITEM(0),
    MIDDLE_ITEM(1),
    LAST_ITEM(2), ;

    companion object {
        private const val FIRST_INDEX = 0
        private const val LAST_INDEX_ADJUSTMENT = 1

        fun fromPosition(
            position: Int,
            totalSize: Int,
        ): TimelineViewType {
            return if (position == FIRST_INDEX) {
                FIRST_ITEM
            } else if (position < totalSize - LAST_INDEX_ADJUSTMENT) {
                MIDDLE_ITEM
            } else {
                LAST_ITEM
            }
        }

        fun byViewType(viewType: Int): TimelineViewType {
            return entries.first { it.viewType == viewType }
        }
    }
}
