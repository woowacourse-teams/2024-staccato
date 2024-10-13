package com.on.staccato.presentation.staccato

interface MomentToolbarHandler {
    fun onUpdateClicked(
        memoryId: Long,
        memoryTitle: String,
    )

    fun onDeleteClicked()
}
