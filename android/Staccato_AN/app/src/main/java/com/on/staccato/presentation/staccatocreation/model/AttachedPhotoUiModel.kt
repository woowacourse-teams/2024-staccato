package com.on.staccato.presentation.staccatocreation.model

import android.net.Uri
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter.Companion.ADD_PHOTO_BUTTON_URI
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter.Companion.ADD_PHOTO_BUTTON_URL

data class AttachedPhotoUiModel(
    val uri: Uri? = null,
    val imageUrl: String? = null,
) {
    fun updateUrl(newUrl: String): AttachedPhotoUiModel {
        return this.copy(
            uri = uri,
            imageUrl = newUrl,
        )
    }

    companion object {
        val addPhotoButton by lazy {
            AttachedPhotoUiModel(
                Uri.parse(ADD_PHOTO_BUTTON_URI),
                ADD_PHOTO_BUTTON_URL,
            )
        }
    }
}
