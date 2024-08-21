package com.woowacourse.staccato.presentation.momentcreation.model

import android.net.Uri
import com.woowacourse.staccato.presentation.momentcreation.adapter.PhotoAttachAdapter.Companion.ADD_PHOTO_BUTTON_URI
import com.woowacourse.staccato.presentation.momentcreation.adapter.PhotoAttachAdapter.Companion.ADD_PHOTO_BUTTON_URL

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
