package com.on.staccato.presentation.staccatocreation.adapter

import com.on.staccato.presentation.common.photo.AttachedPhotoUiModel

fun interface ItemDragListener {
    fun onStopDrag(list: List<AttachedPhotoUiModel>)
}
