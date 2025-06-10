package com.on.staccato.presentation.staccato.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemStaccatoPhotoBinding
import com.on.staccato.presentation.common.photo.originalphoto.OriginalPhotoHandler

class StaccatoPhotoAdapter(private val handler: OriginalPhotoHandler) :
    ListAdapter<String, StaccatoPhotoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StaccatoPhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStaccatoPhotoBinding.inflate(inflater, parent, false)
        return StaccatoPhotoViewHolder(binding, handler)
    }

    override fun onBindViewHolder(
        holder: StaccatoPhotoViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), position)
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
