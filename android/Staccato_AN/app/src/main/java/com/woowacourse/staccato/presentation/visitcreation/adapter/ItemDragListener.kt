package com.woowacourse.staccato.presentation.visitcreation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotoUiModel

interface ItemDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    fun onStopDrag(list: List<AttachedPhotoUiModel>)
}
