package com.on.staccato.presentation.timeline

interface TimelineHandler {
    fun onCategoryClicked(memoryId: Long)

    fun onCategoryCreationClicked()

    fun onSortClicked()
}
