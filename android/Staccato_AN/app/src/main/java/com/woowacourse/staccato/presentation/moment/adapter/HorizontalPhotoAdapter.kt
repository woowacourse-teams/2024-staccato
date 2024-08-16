package com.woowacourse.staccato.presentation.moment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemPhotoHorizontalBinding

class HorizontalPhotoAdapter :
    ListAdapter<String, HorizontalPhotoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HorizontalPhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoHorizontalBinding.inflate(inflater, parent, false)
        return HorizontalPhotoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HorizontalPhotoViewHolder,
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

class HorizontalPhotoViewHolder(private val binding: ItemPhotoHorizontalBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: String) {
        binding.photoUrl = item
    }
}
