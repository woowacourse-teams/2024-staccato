package com.woowacourse.staccato.presentation.visitcreation.dialog

import com.woowacourse.staccato.presentation.visitcreation.model.TravelUiModel

fun interface TravelSelectionHandler {
    fun onConfirmClicked(travelUiModel: TravelUiModel)
}
