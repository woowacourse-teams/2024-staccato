package com.woowacourse.staccato.presentation.visitcreation.dialog

import com.woowacourse.staccato.presentation.visitcreation.model.VisitMemoryUiModel

fun interface TravelSelectionHandler {
    fun onConfirmClicked(travelUiModel: VisitMemoryUiModel)
}
