package com.woowacourse.staccato.presentation.momentcreation.model

import android.net.Uri

data class AttachedPhotoUiModels(
    val attachedPhotos: List<AttachedPhotoUiModel>,
) {
    val size get() = attachedPhotos.size

    fun updateOrAppendPhoto(newPhoto: AttachedPhotoUiModel): AttachedPhotoUiModels {
        val updatedPhotos =
            attachedPhotos.map { currentPhoto ->
                if (currentPhoto.uri == newPhoto.uri) {
                    newPhoto
                } else {
                    currentPhoto
                }
            }
        return AttachedPhotoUiModels(updatedPhotos.take(MAX_PHOTO_NUMBER))
    }

    fun addPhotosByUris(uris: List<Uri>): AttachedPhotoUiModels {
        val currentUris = attachedPhotos.map { it.uri }
        val newUris =
            uris.filterNot { uri ->
                currentUris.contains(uri)
            }
        val combinedPhotos = attachedPhotos + newUris.map { AttachedPhotoUiModel(uri = it) }
        return AttachedPhotoUiModels(combinedPhotos.take(MAX_PHOTO_NUMBER))
    }

    fun removePhoto(targetPhoto: AttachedPhotoUiModel): AttachedPhotoUiModels {
        return AttachedPhotoUiModels(attachedPhotos.filterNot { it == targetPhoto })
    }

    fun getPhotosWithoutUrls(): List<AttachedPhotoUiModel> = attachedPhotos.filter { it.imageUrl == null }

    companion object {
        private const val MAX_PHOTO_NUMBER = 5
    }
}
