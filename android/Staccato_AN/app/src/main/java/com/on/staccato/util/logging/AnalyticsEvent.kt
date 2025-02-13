package com.on.staccato.util.logging

import com.google.firebase.analytics.FirebaseAnalytics

data class AnalyticsEvent<T : Any>(
    val name: String,
    val params: List<Param<T>> = emptyList(),
) {
    companion object {
        const val NAME_BOTTOM_SHEET = "bottom_sheet"
        const val NAME_STACCATO_CREATION = "staccato_creation"
        const val NAME_STACCATO_READ = "staccato_read"
        const val NAME_CAMERA_OR_GALLERY = "camera_or_gallery"
        const val NAME_FRAGMENT_PAGE = "fragment_page"
        const val NAME_NETWORK_ERROR = "network_error"

        const val ITEM_NAME = FirebaseAnalytics.Param.ITEM_NAME

        fun <T : Any> of(
            name: String,
            vararg params: Param<T>,
        ): AnalyticsEvent<T> {
            return AnalyticsEvent(
                name = name,
                params = params.toList(),
            )
        }
    }
}
