package com.on.staccato.presentation.common.photo

import android.net.Uri

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
