package com.on.staccato.presentation.momentcreation.dialog

import com.on.staccato.domain.model.MemoryCandidate
import java.time.LocalDateTime

fun interface MemoryVisitedAtSelectionHandler {
    fun onConfirmClicked(
        memoryUiModel: MemoryCandidate,
        visitedAt: LocalDateTime,
    )
}
