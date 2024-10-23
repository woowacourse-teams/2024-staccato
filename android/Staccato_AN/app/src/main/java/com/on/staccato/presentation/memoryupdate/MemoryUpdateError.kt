package com.on.staccato.presentation.memoryupdate

import android.net.Uri

sealed interface MemoryUpdateError {
    data class MemoryInitialization(val message: String) : MemoryUpdateError

    data class Thumbnail(val message: String, val uri: Uri) : MemoryUpdateError

    data class MemoryUpdate(val message: String) : MemoryUpdateError
}
