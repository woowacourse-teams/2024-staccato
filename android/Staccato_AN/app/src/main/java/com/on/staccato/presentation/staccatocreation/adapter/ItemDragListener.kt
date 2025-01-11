package com.on.staccato.presentation.staccatocreation.adapter

import com.on.staccato.presentation.staccatocreation.model.AttachedPhotoUiModel

fun interface ItemDragListener {
    fun onStopDrag(list: List<AttachedPhotoUiModel>)
}
