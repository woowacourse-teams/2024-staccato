package com.on.staccato.presentation.staccato.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemViewpagePhotoBinding

class ViewpagePhotoAdapter :
    ListAdapter<String, ViewpagePhotoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewpagePhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemViewpagePhotoBinding.inflate(inflater, parent, false)
        return ViewpagePhotoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewpagePhotoViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String,
                ): Boolean = oldItem == newItem
            }
    }
}
