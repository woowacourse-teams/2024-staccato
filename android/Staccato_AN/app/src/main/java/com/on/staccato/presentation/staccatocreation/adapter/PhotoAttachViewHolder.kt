package com.on.staccato.presentation.staccatocreation.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.databinding.ItemAddPhotoBinding
import com.on.staccato.databinding.ItemAttachedPhotoBinding
import com.on.staccato.presentation.common.photo.AttachedPhotoHandler
import com.on.staccato.presentation.common.photo.AttachedPhotoUiModel

sealed class PhotoAttachViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
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
    ) : PhotoAttachViewHolder(binding) {
        fun bind(item: AttachedPhotoUiModel) {
            binding.attachedPhoto = item
            binding.selectedPhotoHandler = attachedPhotoHandler
        }

        fun startMoving() {
            binding.ivAttachedPhoto.alpha = 0.6F
        }

        fun stopMoving() {
            binding.ivAttachedPhoto.alpha = 1F
        }
    }
}
