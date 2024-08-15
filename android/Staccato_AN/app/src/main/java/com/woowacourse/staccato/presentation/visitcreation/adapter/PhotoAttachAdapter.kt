package com.woowacourse.staccato.presentation.visitcreation.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.staccato.databinding.ItemAddPhotoBinding
import com.woowacourse.staccato.databinding.ItemAttachedPhotoBinding
import com.woowacourse.staccato.presentation.common.AttachedPhotoHandler

class PhotoAttachAdapter(private val attachedPhotoHandler: AttachedPhotoHandler) :
    ListAdapter<Uri, PhotoAttachViewHolder>(diffUtil) {
    init {
        submitList(listOf(Uri.parse(TEMP_URI_STRING)))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == ADD_PHOTO_POSITION) {
            VIEW_TYPE_ADD_PHOTO
        } else {
            VIEW_TYPE_ATTACHED_PHOTO
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PhotoAttachViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ADD_PHOTO -> {
                val binding = ItemAddPhotoBinding.inflate(inflater, parent, false)
                PhotoAttachViewHolder.AddPhotoViewHolder(binding, attachedPhotoHandler)
            }

            VIEW_TYPE_ATTACHED_PHOTO -> {
                val binding = ItemAttachedPhotoBinding.inflate(inflater, parent, false)
                PhotoAttachViewHolder.AttachedPhotoViewHolder(binding, attachedPhotoHandler)
            }

            else -> {
                throw IllegalArgumentException("유효하지 않은 뷰타입 입니다.")
            }
        }
    }

    override fun onBindViewHolder(
        holder: PhotoAttachViewHolder,
        position: Int,
    ) {
        if (holder is PhotoAttachViewHolder.AddPhotoViewHolder) {
            holder.bind()
        } else if (holder is PhotoAttachViewHolder.AttachedPhotoViewHolder) {
            holder.bind(getItem(position))
        }
    }

    companion object {
        const val ADD_PHOTO_POSITION = 0
        const val VIEW_TYPE_ADD_PHOTO = 0
        const val VIEW_TYPE_ATTACHED_PHOTO = 1
        const val TEMP_URI_STRING = "tempUri"

        val diffUtil =
            object : DiffUtil.ItemCallback<Uri>() {
                override fun areItemsTheSame(
                    oldItem: Uri,
                    newItem: Uri,
                ): Boolean {
                    return oldItem.toString() == newItem.toString()
                }

                override fun areContentsTheSame(
                    oldItem: Uri,
                    newItem: Uri,
                ): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            }
    }
}
