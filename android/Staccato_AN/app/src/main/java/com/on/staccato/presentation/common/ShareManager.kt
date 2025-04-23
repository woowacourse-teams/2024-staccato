package com.on.staccato.presentation.common

import android.content.Context
import android.content.Intent
import com.on.staccato.R
import javax.inject.Inject

class ShareManager
    @Inject
    constructor(private val context: Context) {
        fun shareStaccato(
            staccatoTitle: String,
            nickname: String,
            url: String,
        ) {
            val shareText = createShareText(nickname, staccatoTitle, url)
            val intent =
                Intent(Intent.ACTION_SEND)
                    .setType(MIME_TYPE_TEXT)
                    .putExtra(Intent.EXTRA_TEXT, shareText)

            val shareDescription = context.getString(R.string.staccato_share)
            val shareIntent = Intent.createChooser(intent, shareDescription)
            context.startActivity(shareIntent)
        }

        private fun createShareText(
            nickname: String,
            staccatoTitle: String,
            url: String,
        ): String {
            val shareTitle = context.getString(R.string.staccato_share_message).format(nickname)
            return buildString {
                appendLine(shareTitle)
                appendLine("[$staccatoTitle]")
                appendLine(url)
            }
        }

        companion object {
            private const val MIME_TYPE_TEXT = "text/plain"
        }
    }
