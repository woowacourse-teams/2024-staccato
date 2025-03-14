package com.on.staccato.util.logging

data class Param<T : Any>(val key: String, val value: T) {
    companion object {
        const val KEY_SDK_VERSION = "Android_SDK_version"

        const val KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        const val PARAM_BOTTOM_SHEET_COLLAPSED = "COLLAPSED"
        const val PARAM_BOTTOM_SHEET_EXPANDED = "EXPANDED"
        const val PARAM_BOTTOM_SHEET_HALF_EXPANDED = "HALF_EXPANDED"
        const val KEY_BOTTOM_SHEET_DURATION = "bottom_sheet_state_duration"

        const val KEY_IS_CREATED_IN_MAIN = "is_created_in_main_activity"
        const val KEY_IS_VIEWED_BY_MARKER = "is_viewed_from_marker_click"
        const val KEY_IS_GALLERY = "is_attached_from_gallery"

        const val KEY_FRAGMENT_NAME = "fragment_name"
        const val PARAM_CATEGORY_LIST = "category_list_timeline"
        const val PARAM_CATEGORY_FRAGMENT = "category_fragment"
        const val PARAM_STACCATO_FRAGMENT = "staccato_fragment"

        const val KEY_EXCEPTION = "exception"
        const val KEY_EXCEPTION_MESSAGE = "exception_message"
    }
}
