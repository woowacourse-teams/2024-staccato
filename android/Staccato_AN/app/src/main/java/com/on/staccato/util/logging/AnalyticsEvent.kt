package com.on.staccato.util.logging

data class AnalyticsEvent<T : Any>(
    val name: String,
    val params: List<Param<T>> = emptyList(),
) {
    companion object {
        const val NAME_BOTTOM_SHEET = "bottom_sheet"
        const val NAME_STACCATO_CREATION = "staccato_creation"
        const val NAME_STACCATO_READ = "staccato_read"
        const val NAME_CAMERA_OR_GALLERY = "camera_or_gallery"
        const val NAME_NETWORK_ERROR = "network_error"
        const val NAME_ANDROID_ERROR = "android_error"

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
