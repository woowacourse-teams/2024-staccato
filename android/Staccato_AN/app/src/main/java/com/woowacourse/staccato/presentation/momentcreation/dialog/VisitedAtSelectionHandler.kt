package com.woowacourse.staccato.presentation.momentcreation.dialog

import java.time.LocalDate

fun interface VisitedAtSelectionHandler {
    fun onConfirmClicked(visitedAt: LocalDate)
}
