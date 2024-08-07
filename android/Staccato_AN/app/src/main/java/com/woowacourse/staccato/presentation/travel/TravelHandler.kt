package com.woowacourse.staccato.presentation.travel

import com.woowacourse.staccato.presentation.common.DialogHandler

interface TravelHandler : DialogHandler {
    fun onVisitClicked(visitId: Long)
}
