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

    fun toLoading() = copy(state = AttachedPhotoState.Loading)

    fun toRetry() = copy(state = AttachedPhotoState.Retry)

    fun toFail() = copy(state = AttachedPhotoState.Failure)

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
