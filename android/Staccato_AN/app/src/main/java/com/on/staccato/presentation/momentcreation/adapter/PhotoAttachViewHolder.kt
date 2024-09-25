package com.on.staccato.presentation.momentcreation.adapter

import android.view.MotionEvent
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.databinding.ItemAddPhotoBinding
import com.on.staccato.databinding.ItemAttachedPhotoBinding
import com.on.staccato.presentation.common.AttachedPhotoHandler
import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.visitcreation.adapter.ItemDragListener

sealed class PhotoAttachViewHolder(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    class AddPhotoViewHolder(
        private val binding: ItemAddPhotoBinding,
        private val attachedPhotoHandler: AttachedPhotoHandler,
    ) :
        PhotoAttachViewHolder(binding) {
        fun bind() {
            binding.selectedPhotoHandler = attachedPhotoHandler
        }
    }

    class AttachedPhotoViewHolder(
        private val binding: ItemAttachedPhotoBinding,
        private val attachedPhotoHandler: AttachedPhotoHandler,
        private val dragListener: ItemDragListener,
    ) : PhotoAttachViewHolder(binding) {
        fun bind(item: AttachedPhotoUiModel) {
            binding.ivAttachedPhoto.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
            binding.attachedPhoto = item
            binding.selectedPhotoHandler = attachedPhotoHandler
        }
    }
}