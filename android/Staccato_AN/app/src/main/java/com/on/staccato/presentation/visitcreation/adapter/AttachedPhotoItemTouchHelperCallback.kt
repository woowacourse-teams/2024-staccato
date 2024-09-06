package com.on.staccato.presentation.visitcreation.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.presentation.momentcreation.adapter.PhotoAttachViewHolder

class AttachedPhotoItemTouchHelperCallback(
    private val moveListener: ItemMoveListener,
) : ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        return makeFlag(
            ItemTouchHelper.ACTION_STATE_DRAG,
            ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END,
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return if (target is PhotoAttachViewHolder.AttachedPhotoViewHolder) {
            moveListener.onItemMove(
                viewHolder.absoluteAdapterPosition,
                target.absoluteAdapterPosition,
            )
            true
        } else {
            false
        }
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int,
    ) {
        super.onSelectedChanged(viewHolder, actionState)
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_IDLE -> moveListener.onStopDrag()
        }
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        // TODO("Not yet implemented")
    }
}
