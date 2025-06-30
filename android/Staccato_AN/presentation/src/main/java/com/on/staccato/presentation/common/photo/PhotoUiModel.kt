package com.on.staccato.presentation.common.photo

import android.net.Uri

data class PhotoUiModel(
    val uri: Uri? = null,
    val imageUrl: String? = null,
    val state: PhotoUploadState,
) {
    fun toSuccessPhotoWith(newUrl: String) =
        copy(
            imageUrl = newUrl,
            state = PhotoUploadState.Success,
        )

    fun toLoading() = copy(state = PhotoUploadState.Loading)

    fun toRetry() = copy(state = PhotoUploadState.Retry)

    fun toFail() = copy(state = PhotoUploadState.Failure)

    companion object {
        fun ImageUrl.toSuccessPhoto(): PhotoUiModel =
            PhotoUiModel(
                imageUrl = this,
                state = PhotoUploadState.Success,
            )

        fun Uri.toLoadingPhoto(): PhotoUiModel =
            PhotoUiModel(
                uri = this,
                state = PhotoUploadState.Loading,
            )
    }
}

private typealias ImageUrl = String
