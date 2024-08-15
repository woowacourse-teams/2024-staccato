package com.woowacourse.staccato.presentation.visitcreation.adapter

import android.net.Uri
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemAddPhotoBinding
import com.woowacourse.staccato.databinding.ItemAttachedPhotoBinding
import com.woowacourse.staccato.presentation.common.AttachedPhotoHandler

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
    ) : PhotoAttachViewHolder(binding) {
        fun bind(item: Uri) {
            binding.attachedPhoto = item
            binding.selectedPhotoHandler = attachedPhotoHandler
        }
    }
}
