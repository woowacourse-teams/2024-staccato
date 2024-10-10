package com.on.staccato.presentation.momentcreation.dialog

import java.time.LocalDateTime

fun interface VisitedAtSelectionHandler {
    fun onConfirmClicked(visitedAt: LocalDateTime)
}
