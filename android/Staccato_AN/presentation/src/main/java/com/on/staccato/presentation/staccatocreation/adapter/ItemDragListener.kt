package com.on.staccato.presentation.staccatocreation.adapter

import com.on.staccato.presentation.common.photo.PhotoUiModel

fun interface ItemDragListener {
    fun onStopDrag(list: List<PhotoUiModel>)
}
