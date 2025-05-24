package com.on.staccato.presentation.staccatocreation.adapter

import android.graphics.Canvas
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class AttachedPhotoItemTouchHelperCallback(
    private val moveListener: ItemMoveListener,
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        return if (viewHolder is PhotoAttachViewHolder.AttachedPhotoViewHolder) {
            makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END,
            )
        } else {
            ItemTouchHelper.ACTION_STATE_IDLE
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
            ViewCompat.setElevation(viewHolder.itemView, DRAGGED_ITEM_ELEVATION)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                if (viewHolder is PhotoAttachViewHolder.AttachedPhotoViewHolder) {
                    viewHolder.startMoving()
                }
            }

            ItemTouchHelper.ACTION_STATE_IDLE -> {
                moveListener.onStopDrag()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        if (viewHolder is PhotoAttachViewHolder.AttachedPhotoViewHolder) {
            viewHolder.stopMoving()
            ViewCompat.setElevation(viewHolder.itemView, IDLE_ITEM_ELEVATION)
        }
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        // TODO("Not yet implemented")
    }

    companion object {
        private const val IDLE_ITEM_ELEVATION = 0f
        private const val DRAGGED_ITEM_ELEVATION = 10f
    }
}
