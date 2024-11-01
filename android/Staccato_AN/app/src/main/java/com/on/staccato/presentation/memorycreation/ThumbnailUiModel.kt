package com.on.staccato.presentation.memorycreation

import android.net.Uri

data class ThumbnailUiModel(
    val uri: Uri? = null,
    val url: String? = null,
) {
    fun updateUrl(newUrl: String?): ThumbnailUiModel = this.copy(url = newUrl)

    fun isEqualUri(newUri: Uri?): Boolean = uri == newUri

    fun updateThumbnail(
        newUri: Uri?,
        newUrl: String?,
    ) = this.copy(uri = newUri, url = newUrl)

    fun delete() = this.copy(uri = null, url = null)
}
