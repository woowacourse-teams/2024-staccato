package com.woowacourse.staccato.presentation.common

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemPhotoAttachBinding

class PhotoAttachAdapter(private val selectedPhotoHandler: SelectedPhotoHandler) :
    ListAdapter<Uri, PhotoAttachViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PhotoAttachViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoAttachBinding.inflate(inflater, parent, false)
        return PhotoAttachViewHolder(binding, selectedPhotoHandler)
    }

    override fun onBindViewHolder(
        holder: PhotoAttachViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<Uri>() {
                override fun areItemsTheSame(
                    oldItem: Uri,
                    newItem: Uri,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: Uri,
                    newItem: Uri,
                ): Boolean = oldItem == newItem
            }
    }
}

class PhotoAttachViewHolder(
    private val binding: ItemPhotoAttachBinding,
    private val selectedPhotoHandler: SelectedPhotoHandler,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Uri) {
        binding.attachedPhoto = item
        binding.selectedPhotoHandler = selectedPhotoHandler
    }
}

interface SelectedPhotoHandler {
    fun onDeleteClicked(item: Uri)
}
