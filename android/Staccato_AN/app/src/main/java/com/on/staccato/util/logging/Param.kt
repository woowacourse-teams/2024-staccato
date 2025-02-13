package com.on.staccato.util.logging

data class Param<T : Any>(val key: String, val value: T) {
    companion object {
        const val KEY_SDK_VERSION = "안드로이드 SDK 버전"

        const val KEY_BOTTOM_SHEET_STATE = "바텀시트 상태 종류"
        const val PARAM_BOTTOM_SHEET_COLLAPSED = "COLLAPSED"
        const val PARAM_BOTTOM_SHEET_EXPANDED = "EXPANDED"
        const val PARAM_BOTTOM_SHEET_HALF_EXPANDED = "HALF_EXPANDED"
        const val KEY_BOTTOM_SHEET_DURATION = "지속시간"

        const val KEY_IS_CREATED_IN_MAIN = "메인 화면에서 만들어진 스타카토인가?"
        const val KEY_IS_VIEWED_BY_MARKER = "마커 클릭으로 조회된 스타카토인가?"
        const val KEY_IS_GALLERY = "갤러리에서 사진을 첨부했나?"

        const val KEY_EXCEPTION = "예외"
        const val KEY_EXCEPTION_MESSAGE = "예외 메시지"

        fun <T : Any> of(
            key: String,
            value: T,
        ): Param<T> = Param(key, value)
    }
}
