package com.woowacourse.staccato.presentation.timeline.adapter

enum class TimelineViewType {
    FIRST_ITEM,
    MIDDLE_ITEM,
    LAST_ITEM, ;

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
    }
}
