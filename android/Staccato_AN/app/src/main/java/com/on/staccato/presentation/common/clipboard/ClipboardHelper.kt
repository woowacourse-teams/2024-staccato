package com.on.staccato.presentation.common.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import com.on.staccato.R
import com.on.staccato.presentation.util.showToast
import javax.inject.Inject

class ClipboardHelper
    @Inject
    constructor(
        private val clipboardManager: ClipboardManager,
    ) {
        fun copyText(
            label: String,
            text: String,
            context: Context,
        ) {
            val clipData: ClipData = ClipData.newPlainText(label, text)
            clipboardManager.setPrimaryClip(clipData)
            context.showCopySuccessMessage()
        }

        private fun Context.showCopySuccessMessage() {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                val message = resources.getString(R.string.all_clipboard_copy)
                showToast(message)
            }
        }
    }
