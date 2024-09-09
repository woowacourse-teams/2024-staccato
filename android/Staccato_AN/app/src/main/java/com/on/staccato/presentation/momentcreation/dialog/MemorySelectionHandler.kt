package com.on.staccato.presentation.momentcreation.dialog

import com.on.staccato.domain.model.MemoryCandidate

fun interface MemorySelectionHandler {
    fun onConfirmClicked(memoryUiModel: MemoryCandidate)
}
