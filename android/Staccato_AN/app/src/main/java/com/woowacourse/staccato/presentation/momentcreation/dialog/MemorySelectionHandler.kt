package com.woowacourse.staccato.presentation.momentcreation.dialog

import com.woowacourse.staccato.presentation.momentcreation.model.MomentMemoryUiModel

fun interface MemorySelectionHandler {
    fun onConfirmClicked(memoryUiModel: MomentMemoryUiModel)
}
