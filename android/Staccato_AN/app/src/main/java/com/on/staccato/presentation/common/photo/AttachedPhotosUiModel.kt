package com.on.staccato.presentation.common.photo

import android.net.Uri
import com.on.staccato.presentation.common.photo.AttachedPhotoUiModel.Companion.toLoadingPhoto
import com.on.staccato.presentation.common.photo.AttachedPhotoUiModel.Companion.toSuccessPhoto

data class AttachedPhotosUiModel(
    val attachedPhotos: List<AttachedPhotoUiModel>,
) {
    val size get() = attachedPhotos.size

    fun updateOrAppendPhoto(newPhoto: AttachedPhotoUiModel): AttachedPhotosUiModel {
        val updatedPhotos =
            attachedPhotos.map { currentPhoto ->
                if (currentPhoto.uri == newPhoto.uri) {
                    newPhoto
                } else {
                    currentPhoto
                }
            }
        return AttachedPhotosUiModel(updatedPhotos.take(MAX_PHOTO_NUMBER))
    }

    fun addPhotosByUris(uris: List<Uri>): AttachedPhotosUiModel {
        val currentUris = attachedPhotos.map { it.uri }
        val newUris =
            uris.filterNot { uri ->
                currentUris.contains(uri)
            }
        val combinedPhotos = attachedPhotos + newUris.map { newUri -> newUri.toLoadingPhoto() }
        return AttachedPhotosUiModel(combinedPhotos.take(MAX_PHOTO_NUMBER))
    }

    fun removePhoto(targetPhoto: AttachedPhotoUiModel) = AttachedPhotosUiModel(attachedPhotos.filterNot { it == targetPhoto })

    fun toLoading(targetPhoto: AttachedPhotoUiModel) =
        AttachedPhotosUiModel(
            attachedPhotos.map { if (it == targetPhoto) it.toLoading() else it },
        )

    fun getLoadingPhotosWithoutUrls(): List<AttachedPhotoUiModel> {
        return attachedPhotos.filter { it.imageUrl == null && it.state == AttachedPhotoState.Loading }
    }

    fun hasNotSuccessPhoto(): Boolean = attachedPhotos.isNotEmpty() && attachedPhotos.any { it.state != AttachedPhotoState.Success }

    companion object {
        const val MAX_PHOTO_NUMBER = 8

        fun ImageUrls.toSuccessPhotos() = AttachedPhotosUiModel(map { url -> url.toSuccessPhoto() })
    }
}

private typealias ImageUrls = List<String>
