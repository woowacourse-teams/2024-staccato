package com.on.staccato.presentation.staccatocreation.dialog

import java.time.LocalDateTime

fun interface VisitedAtSelectionHandler {
    fun onConfirmClicked(visitedAt: LocalDateTime)
}
