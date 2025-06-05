package com.on.staccato.presentation.staccato.comments

import kotlin.IllegalArgumentException

enum class CommentViewType(val viewType: Int) {
    MY_COMMENT(0),
    OTHER_COMMENT(1), ;

    companion object {
        fun from(viewType: Int): CommentViewType =
            when (viewType) {
                0 -> MY_COMMENT
                1 -> OTHER_COMMENT
                else -> throw IllegalArgumentException("유효하지 않은 CommentViewType 입니다.")
            }
    }
}
