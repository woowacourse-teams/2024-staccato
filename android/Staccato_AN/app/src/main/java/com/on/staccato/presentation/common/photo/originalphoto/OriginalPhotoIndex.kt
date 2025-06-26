package com.on.staccato.presentation.common.photo.originalphoto

@JvmInline
value class OriginalPhotoIndex(val initialPage: Int) {
    val isAvailable: Boolean
        get() = (initialPage != INVALID_POSITION)

    companion object {
        private const val INVALID_POSITION = -1
        val unavailable = OriginalPhotoIndex(INVALID_POSITION)
    }
}
