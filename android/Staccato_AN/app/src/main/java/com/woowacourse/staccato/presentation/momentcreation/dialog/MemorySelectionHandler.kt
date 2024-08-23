package com.woowacourse.staccato.presentation.momentcreation.dialog

import com.woowacourse.staccato.domain.model.MemoryCandidate

fun interface MemorySelectionHandler {
    fun onConfirmClicked(memoryUiModel: MemoryCandidate)
}
