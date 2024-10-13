package com.on.staccato.presentation.staccato

interface StaccatoToolbarHandler {
    fun onUpdateClicked(
        memoryId: Long,
        memoryTitle: String,
    )

    fun onDeleteClicked()
}
