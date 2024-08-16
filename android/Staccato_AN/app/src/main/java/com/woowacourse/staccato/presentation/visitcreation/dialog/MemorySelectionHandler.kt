package com.woowacourse.staccato.presentation.visitcreation.dialog

import com.woowacourse.staccato.presentation.visitcreation.model.MomentMemoryUiModel

fun interface MemorySelectionHandler {
    fun onConfirmClicked(memoryUiModel: MomentMemoryUiModel)
}
