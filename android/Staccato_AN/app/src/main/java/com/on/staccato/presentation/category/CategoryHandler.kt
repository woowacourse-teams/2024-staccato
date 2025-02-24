package com.on.staccato.presentation.category

import com.on.staccato.presentation.category.model.CategoryUiModel

interface CategoryHandler {
    fun onStaccatoClicked(staccatoId: Long)

    fun onStaccatoCreationClicked(
        category: CategoryUiModel,
        isPermissionCanceled: Boolean,
    )
}
