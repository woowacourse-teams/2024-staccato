package com.on.staccato.presentation.staccatocreation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.presentation.databinding.ItemAddPhotoBinding
import com.on.staccato.presentation.databinding.ItemAttachedPhotoBinding
import com.on.staccato.presentation.photo.AttachedPhotoHandler
import com.on.staccato.presentation.photo.PhotoUiModel
import com.on.staccato.presentation.photo.PhotoUploadState

class PhotoAttachAdapter(
    private val attachedPhotoHandler: AttachedPhotoHandler,
    private val dragListener: ItemDragListener,
) :
    ItemMoveListener, ListAdapter<PhotoUiModel, PhotoAttachViewHolder>(diffUtil) {
    init {
        submitList(listOf(photoAdditionButton))
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
        dragListener.onStopDrag(currentList.filterNot { it == photoAdditionButton })
    }

    companion object {
        private const val ADD_PHOTO_BUTTON_URI = "add_photo_button_uri"
        private const val ADD_PHOTO_BUTTON_URL = "add_photo_button_url"
        const val ADD_PHOTO_POSITION = 0
        const val VIEW_TYPE_ADD_PHOTO = 0
        const val VIEW_TYPE_ATTACHED_PHOTO = 1

        val photoAdditionButton by lazy {
            PhotoUiModel(
                ADD_PHOTO_BUTTON_URI.toUri(),
                ADD_PHOTO_BUTTON_URL,
                PhotoUploadState.Success,
            )
        }

        val diffUtil =
            object : DiffUtil.ItemCallback<PhotoUiModel>() {
                override fun areItemsTheSame(
                    oldItem: PhotoUiModel,
                    newItem: PhotoUiModel,
                ): Boolean {
                    return if (oldItem.uri == null && newItem.uri == null) {
                        oldItem.imageUrl == newItem.imageUrl
                    } else {
                        oldItem.uri == newItem.uri
                    }
                }

                override fun areContentsTheSame(
                    oldItem: PhotoUiModel,
                    newItem: PhotoUiModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
