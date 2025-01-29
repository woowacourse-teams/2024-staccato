package com.on.staccato.presentation.staccato

interface StaccatoToolbarHandler {
    fun onUpdateClicked(
        categoryId: Long,
        categoryTitle: String,
    )

    fun onDeleteClicked()
}
