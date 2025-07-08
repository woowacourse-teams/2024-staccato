package com.on.staccato.presentation.photo

import android.net.Uri
import com.on.staccato.presentation.photo.PhotoUiModel.Companion.toLoadingPhoto
import com.on.staccato.presentation.photo.PhotoUiModel.Companion.toSuccessPhoto

data class PhotosUiModel(
    val photos: List<PhotoUiModel>,
) {
    val size get() = photos.size

    fun imageUrls(): List<String> = photos.mapNotNull { it.imageUrl }

    fun updatePhoto(updatedPhoto: PhotoUiModel): PhotosUiModel {
        val updatedPhotos =
            photos.map { currentPhoto ->
                if (currentPhoto.uri == updatedPhoto.uri) {
                    updatedPhoto
                } else {
                    currentPhoto
                }
            }
        return PhotosUiModel(updatedPhotos.take(MAX_PHOTO_NUMBER))
    }

    fun addPhotosByUris(uris: List<Uri>): PhotosUiModel {
        val currentUris = photos.map { it.uri }
        val newUris =
            uris.filterNot { uri ->
                currentUris.contains(uri)
            }
        val combinedPhotos = photos + newUris.map { newUri -> newUri.toLoadingPhoto() }
        return PhotosUiModel(combinedPhotos.take(MAX_PHOTO_NUMBER))
    }

    fun removePhoto(targetPhoto: PhotoUiModel) = PhotosUiModel(photos.filterNot { it == targetPhoto })

    fun toLoading(targetPhoto: PhotoUiModel) =
        PhotosUiModel(
            photos.map { if (it == targetPhoto) it.toLoading() else it },
        )

    fun getLoadingPhotosWithoutUrls(): List<PhotoUiModel> {
        return photos.filter { it.imageUrl == null && it.state == PhotoUploadState.Loading }
    }

    fun hasNotSuccessPhoto(): Boolean = photos.isNotEmpty() && photos.any { it.state != PhotoUploadState.Success }

    companion object {
        const val MAX_PHOTO_NUMBER = 8

        fun ImageUrls.toSuccessPhotos() = PhotosUiModel(map { url -> url.toSuccessPhoto() })
    }
}

private typealias ImageUrls = List<String>
