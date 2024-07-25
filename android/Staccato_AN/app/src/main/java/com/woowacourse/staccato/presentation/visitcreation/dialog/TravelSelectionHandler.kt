package com.woowacourse.staccato.presentation.visitcreation.dialog

import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel

fun interface TravelSelectionHandler {
    fun onConfirmClicked(travelUiModel: VisitTravelUiModel)
}
