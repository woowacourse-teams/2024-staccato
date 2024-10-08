package com.on.staccato.presentation.timeline

interface TimelineHandler {
    fun onMemoryClicked(memoryId: Long)

    fun onMemoryCreationClicked()

    fun onSortClicked()
}
