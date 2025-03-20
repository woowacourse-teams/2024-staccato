package com.on.staccato.presentation.timeline

interface TimelineHandler {
    fun onCategoryClicked(categoryId: Long)

    fun onCategoryCreationClicked()

    fun onSortClicked()

    fun onFilterClicked()
}
