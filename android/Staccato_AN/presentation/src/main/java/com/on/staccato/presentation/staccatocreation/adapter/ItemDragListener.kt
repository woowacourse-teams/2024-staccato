package com.on.staccato.presentation.staccatocreation.adapter

import com.on.staccato.presentation.photo.PhotoUiModel

fun interface ItemDragListener {
    fun onStopDrag(list: List<PhotoUiModel>)
}
