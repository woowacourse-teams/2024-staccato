package com.on.staccato.presentation.staccatocreation.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class AttachedPhotoItemTouchHelperCallback(
    private val moveListener: ItemMoveListener,
) : ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return true
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
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                if (viewHolder != null) {
                    (viewHolder as PhotoAttachViewHolder.AttachedPhotoViewHolder).startMoving()
                }
            }

            ItemTouchHelper.ACTION_STATE_IDLE -> {
                moveListener.onStopDrag()
            }
        }
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        (viewHolder as PhotoAttachViewHolder.AttachedPhotoViewHolder).stopMoving()
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        // TODO("Not yet implemented")
    }
}
