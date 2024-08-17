package com.woowacourse.staccato.presentation.visitcreation.adapter

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView

interface ItemDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    fun onStopDrag(list: List<Uri>)
}
