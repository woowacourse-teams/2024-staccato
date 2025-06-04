package com.on.staccato.presentation.common.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import com.on.staccato.R
import com.on.staccato.presentation.util.showToast
import javax.inject.Inject

class ClipboardHelper
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val clipboardManager: ClipboardManager by lazy {
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        }

        fun copyText(
            label: String,
            text: String,
        ) {
            val clipData: ClipData = ClipData.newPlainText(label, text)
            clipboardManager.setPrimaryClip(clipData)
            showCopySuccessMessage()
        }

        private fun showCopySuccessMessage() {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                val message = context.resources.getString(R.string.all_clipboard_copy)
                context.showToast(message)
            }
        }
    }
