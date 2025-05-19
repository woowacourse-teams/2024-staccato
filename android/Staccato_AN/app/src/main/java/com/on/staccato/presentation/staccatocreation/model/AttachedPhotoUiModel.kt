package com.on.staccato.presentation.staccatocreation.model

import android.net.Uri
import androidx.core.net.toUri
import com.on.staccato.presentation.common.photo.AttachedPhotoState
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter.Companion.ADD_PHOTO_BUTTON_URI
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter.Companion.ADD_PHOTO_BUTTON_URL

data class AttachedPhotoUiModel(
    val uri: Uri? = null,
    val imageUrl: String? = null,
    val state: AttachedPhotoState,
) {
    fun toSuccessPhotoWith(newUrl: String) =
        copy(
            imageUrl = newUrl,
            state = AttachedPhotoState.Success,
        )

    fun toRetry() = copy(state = AttachedPhotoState.Retry)

    fun toFail() = copy(state = AttachedPhotoState.Fail)

    companion object {
        val addPhotoButton by lazy {
            AttachedPhotoUiModel(
                ADD_PHOTO_BUTTON_URI.toUri(),
                ADD_PHOTO_BUTTON_URL,
                AttachedPhotoState.Success,
            )
        }

        fun ImageUrl.toSuccessPhoto(): AttachedPhotoUiModel =
            AttachedPhotoUiModel(
                imageUrl = this,
                state = AttachedPhotoState.Success,
            )

        fun Uri.toLoadingPhoto(): AttachedPhotoUiModel =
            AttachedPhotoUiModel(
                uri = this,
                state = AttachedPhotoState.Loading,
            )
    }
}

private typealias ImageUrl = String
