package com.woowacourse.staccato.presentation.visitcreation.dialog

import java.time.LocalDate

fun interface VisitedAtSelectionHandler {
    fun onConfirmClicked(visitedAt: LocalDate)
}
