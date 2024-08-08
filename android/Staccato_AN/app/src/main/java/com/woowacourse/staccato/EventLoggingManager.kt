package com.woowacourse.staccato

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

class EventLoggingManager(
    private val firebaseAnalytics: FirebaseAnalytics,
) {
    fun logEvent(
        id: String,
        name: String,
        contentType: String,
    ) {
        val parameters =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, name)
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            }
        firebaseAnalytics.setDefaultEventParameters(parameters)
    }

    fun logReadingCountEvent(
        itemId: Long,
        nickname: String,
        contentType: String,
    ) {
        val parameters =
            Bundle().apply {
                putLong(FirebaseAnalytics.Param.ITEM_ID, itemId)
                putString(KEY_NICKNAME, nickname)
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            }
        firebaseAnalytics.setDefaultEventParameters(parameters)
    }

    fun startScreenLogging(
        screenName: String,
        screenClass: String,
    ) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }

    companion object {
        const val NAME_TRAVEL_CREATION = "여행 생성"
        const val NAME_TRAVEL_UPDATE = "여행 수정"
        const val NAME_TRAVEL_DELETE = "여행 삭제"

        const val CONTENT_TYPE_BUTTON = "Button"
        const val CONTENT_TYPE_TRAVEL = "Travel"

        const val KEY_NICKNAME = "nickname"
    }
}
