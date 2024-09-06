package com.on.staccato.presentation.moment.detail

import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.databinding.ItemViewpagePhotoBinding

class ViewpagePhotoViewHolder(private val binding: ItemViewpagePhotoBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: String) {
        binding.photoUrl = item
    }
}
