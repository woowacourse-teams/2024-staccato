package com.woowacourse.staccato.presentation.common

import android.net.Uri

interface AttachedPhotoHandler {
    fun onDeleteClicked(deletedUri: Uri)

    fun onAddClicked()
}
