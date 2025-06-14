package com.on.staccato.presentation.staccato.detail

import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.databinding.ItemStaccatoPhotoBinding
import com.on.staccato.presentation.common.photo.originalphoto.OriginalPhotoHandler

class StaccatoPhotoViewHolder(
    private val binding: ItemStaccatoPhotoBinding,
    private val handler: OriginalPhotoHandler,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: String,
        position: Int,
    ) {
        binding.photoUrl = item
        binding.handler = handler
        binding.position = position
    }
}
