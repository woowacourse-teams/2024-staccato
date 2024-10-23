package com.on.staccato.presentation.memorycreation

import android.net.Uri

sealed interface MemoryCreationError {
    data class Thumbnail(val message: String, val uri: Uri) : MemoryCreationError

    data class MemoryCreation(val message: String) : MemoryCreationError
}
