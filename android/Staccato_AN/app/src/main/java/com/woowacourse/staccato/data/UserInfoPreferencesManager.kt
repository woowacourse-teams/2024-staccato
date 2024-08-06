package com.woowacourse.staccato.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserInfoPreferencesManager(context: Context) {
    private val mUserInfoPrefs: SharedPreferences =
        context.getSharedPreferences(USER_INFO_PREF_NAME, Context.MODE_PRIVATE)

    suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            mUserInfoPrefs.getString(TOKEN_KEY_NAME, "")
        }
    }

    suspend fun setToken(newToken: String) {
        withContext(Dispatchers.IO) {
            mUserInfoPrefs.edit().putString(TOKEN_KEY_NAME, newToken).apply()
        }
    }

    companion object {
        private const val USER_INFO_PREF_NAME = "com.woowacourse.staccato.user_info_prefs"
        private const val TOKEN_KEY_NAME = "com.woowacourse.staccato.token"
    }
}
