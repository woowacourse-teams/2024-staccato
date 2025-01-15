package com.on.staccato.presentation.staccatocreation.dialog

import com.on.staccato.domain.model.CategoryCandidate

fun interface MemorySelectionHandler {
    fun onConfirmClicked(memoryUiModel: CategoryCandidate)
}
