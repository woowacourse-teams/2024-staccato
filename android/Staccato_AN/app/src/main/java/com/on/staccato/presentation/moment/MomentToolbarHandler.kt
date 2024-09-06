package com.on.staccato.presentation.moment

interface MomentToolbarHandler {
    fun onUpdateClicked(
        memoryId: Long,
        memoryTitle: String,
    )

    fun onDeleteClicked()
}
