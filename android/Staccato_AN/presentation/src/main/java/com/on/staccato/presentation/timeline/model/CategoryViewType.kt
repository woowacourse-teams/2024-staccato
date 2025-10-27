package com.on.staccato.presentation.timeline.model

enum class CategoryViewType {
    CommonWithPeriod,
    CommonWithoutPeriod,
    PersonalWithPeriod,
    PersonalWithoutPeriod,
}

val CategoryUiModel.viewType: CategoryViewType
    get() =
        when {
            isShared && (startAt != null && endAt != null) -> CategoryViewType.CommonWithPeriod
            isShared && (startAt == null && endAt == null) -> CategoryViewType.CommonWithoutPeriod
            !isShared && (startAt != null && endAt != null) -> CategoryViewType.PersonalWithPeriod
            else -> CategoryViewType.PersonalWithoutPeriod
        }
