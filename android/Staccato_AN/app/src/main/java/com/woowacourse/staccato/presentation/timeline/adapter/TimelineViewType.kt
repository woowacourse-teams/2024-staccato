package com.woowacourse.staccato.presentation.timeline.adapter

enum class TimelineViewType {
    WITH_THUMBNAIL, NO_THUMBNAIL;

    companion object {
        fun from(thumbnailUrl: String?): TimelineViewType {
            return if (thumbnailUrl.isNullOrEmpty()) {
                NO_THUMBNAIL
            } else {
                WITH_THUMBNAIL
            }
        }
    }
}
