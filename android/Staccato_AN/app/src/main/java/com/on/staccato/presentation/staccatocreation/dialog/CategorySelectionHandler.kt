package com.on.staccato.presentation.staccatocreation.dialog

import com.on.staccato.domain.model.CategoryCandidate

fun interface CategorySelectionHandler {
    fun onConfirmClicked(categoryUiModel: CategoryCandidate)
}
