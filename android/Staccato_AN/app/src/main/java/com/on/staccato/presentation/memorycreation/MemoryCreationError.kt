package com.on.staccato.presentation.memorycreation

import android.net.Uri

sealed interface MemoryCreationError {
    data class Photo(val message: String, val uri: Uri) : MemoryCreationError

    data class MemoryCreate(val message: String) : MemoryCreationError
}
