package com.on.staccato.presentation.visitcreation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel

interface ItemDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    fun onStopDrag(list: List<AttachedPhotoUiModel>)
}
