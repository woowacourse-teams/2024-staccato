package com.on.staccato.presentation.categorycreation.model

import android.net.Uri

data class ThumbnailUiModel(
    val uri: Uri? = null,
    val url: String? = null,
) {
    fun updateUrl(newUrl: String?): ThumbnailUiModel = this.copy(url = newUrl)

    fun isEqualUri(newUri: Uri?): Boolean = uri == newUri

    fun clear() = this.copy(uri = null, url = null)
}
