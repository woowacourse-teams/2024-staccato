package com.on.staccato.presentation.momentcreation.model

import android.net.Uri

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
        val combinedPhotos = attachedPhotos + newUris.map { AttachedPhotoUiModel(uri = it) }
        return AttachedPhotosUiModel(combinedPhotos.take(MAX_PHOTO_NUMBER))
    }

    fun removePhoto(targetPhoto: AttachedPhotoUiModel): AttachedPhotosUiModel {
        return AttachedPhotosUiModel(attachedPhotos.filterNot { it == targetPhoto })
    }

    fun getPhotosWithoutUrls(): List<AttachedPhotoUiModel> {
        return attachedPhotos.filter { it.imageUrl == null }
    }

    fun isLoading(): Boolean {
        return (attachedPhotos.isNotEmpty()) && (getPhotosWithoutUrls().isNotEmpty())
    }

    companion object {
        private const val MAX_PHOTO_NUMBER = 5

        fun createPhotosByUrls(urls: List<String>): AttachedPhotosUiModel {
            return AttachedPhotosUiModel(urls.map { AttachedPhotoUiModel(imageUrl = it) })
        }
    }
}
