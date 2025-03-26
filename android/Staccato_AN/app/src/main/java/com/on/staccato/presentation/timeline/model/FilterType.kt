package com.on.staccato.presentation.timeline.model

enum class FilterType {
    WITH_TERM,
    WITHOUT_TERM,
    ;

    companion object {
        fun next(filterType: FilterType?): FilterType? =
            when (filterType) {
                WITH_TERM -> WITHOUT_TERM
                WITHOUT_TERM -> null
                else -> WITH_TERM
            }
    }
}
