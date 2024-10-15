package com.on.staccato.presentation.staccatocreation.dialog

import com.on.staccato.domain.model.MemoryCandidate

fun interface MemorySelectionHandler {
    fun onConfirmClicked(memoryUiModel: MemoryCandidate)
}
