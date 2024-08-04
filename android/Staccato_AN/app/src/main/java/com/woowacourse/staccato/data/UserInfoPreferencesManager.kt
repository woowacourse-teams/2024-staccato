package com.woowacourse.staccato.data

import android.content.Context
import android.content.SharedPreferences

class UserInfoPreferencesManager(context: Context) {
    private val mUserInfoPrefs: SharedPreferences =
        context.getSharedPreferences(USER_INFO_PREF_NAME, Context.MODE_PRIVATE)

    fun getToken(): String? {
        return mUserInfoPrefs.getString(TOKEN_KEY_NAME, "")
    }

    fun setToken(newToken: String) {
        mUserInfoPrefs.edit().putString(TOKEN_KEY_NAME, newToken).apply()
    }

    companion object {
        private const val USER_INFO_PREF_NAME = "com.woowacourse.staccato.user_info_prefs"
        private const val TOKEN_KEY_NAME = "com.woowacourse.staccato.token"
    }
}
