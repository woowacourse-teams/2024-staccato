package com.on.staccato.presentation.staccatocreation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemAddPhotoBinding
import com.on.staccato.databinding.ItemAttachedPhotoBinding
import com.on.staccato.presentation.common.AttachedPhotoHandler
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotoUiModel

class PhotoAttachAdapter(
    private val dragListener: ItemDragListener,
    private val attachedPhotoHandler: AttachedPhotoHandler,
) :
    ItemMoveListener, ListAdapter<AttachedPhotoUiModel, PhotoAttachViewHolder>(diffUtil) {
    init {
        submitList(listOf(AttachedPhotoUiModel.addPhotoButton))
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
                PhotoAttachViewHolder.AttachedPhotoViewHolder(
                    binding,
                    attachedPhotoHandler,
                )
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

    override fun onItemMove(
        from: Int,
        to: Int,
    ) {
        val movedItem = currentList[from]
        submitList(
            currentList.toMutableList().apply {
                removeAt(from)
                add(to, movedItem)
            },
        )
    }

    override fun onStopDrag() {
        dragListener.onStopDrag(currentList.filterNot { it == AttachedPhotoUiModel.addPhotoButton })
    }

    companion object {
        const val ADD_PHOTO_BUTTON_URI = "add_photo_button_uri"
        const val ADD_PHOTO_BUTTON_URL = "add_photo_button_url"
        const val ADD_PHOTO_POSITION = 0
        const val VIEW_TYPE_ADD_PHOTO = 0
        const val VIEW_TYPE_ATTACHED_PHOTO = 1

        val diffUtil =
            object : DiffUtil.ItemCallback<AttachedPhotoUiModel>() {
                override fun areItemsTheSame(
                    oldItem: AttachedPhotoUiModel,
                    newItem: AttachedPhotoUiModel,
                ): Boolean {
                    return if (oldItem.uri == null && newItem.uri == null) {
                        oldItem.imageUrl == newItem.imageUrl
                    } else {
                        oldItem.uri == newItem.uri
                    }
                }

                override fun areContentsTheSame(
                    oldItem: AttachedPhotoUiModel,
                    newItem: AttachedPhotoUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
