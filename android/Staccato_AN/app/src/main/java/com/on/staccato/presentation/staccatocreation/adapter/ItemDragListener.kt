package com.on.staccato.presentation.staccatocreation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotoUiModel

interface ItemDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    fun onStopDrag(list: List<AttachedPhotoUiModel>)
}
