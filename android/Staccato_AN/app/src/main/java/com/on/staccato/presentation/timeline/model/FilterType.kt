package com.on.staccato.presentation.timeline.model

enum class FilterType {
    TERM,
    ;

    companion object {
        fun next(filterType: FilterType?): FilterType? =
            if (filterType == TERM) {
                null
            } else {
                TERM
            }
    }
}
