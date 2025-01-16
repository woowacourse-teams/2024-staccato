package com.on.staccato.data

import android.content.Context
import android.content.SharedPreferences
import com.on.staccato.domain.model.MemberProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserInfoPreferencesManager(context: Context) {
    private val mUserInfoPrefs: SharedPreferences =
        context.getSharedPreferences(USER_INFO_PREF_NAME, Context.MODE_PRIVATE)

    suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            mUserInfoPrefs.getString(TOKEN_KEY_NAME, EMPTY_STRING)
        }
    }

    suspend fun getMemberProfile(): MemberProfile =
        MemberProfile(
            profileImageUrl = getProfileImageUrl() ?: EMPTY_STRING,
            nickname = getNickname() ?: EMPTY_STRING,
            uuidCode = getRecoveryCode() ?: EMPTY_STRING,
        )

    private suspend fun getProfileImageUrl(): String? {
        return withContext(Dispatchers.IO) {
            mUserInfoPrefs.getString(PROFILE_IMAGE_URL_KEY_NAME, EMPTY_STRING)
        }
    }

    private suspend fun getNickname(): String? {
        return withContext(Dispatchers.IO) {
            mUserInfoPrefs.getString(NICKNAME_KEY_NAME, null)
        }
    }

    private suspend fun getRecoveryCode(): String? {
        return withContext(Dispatchers.IO) {
            mUserInfoPrefs.getString(RECOVERY_CODE_KEY_NAME, EMPTY_STRING)
        }
    }

    suspend fun setToken(newToken: String) {
        withContext(Dispatchers.IO) {
            mUserInfoPrefs.edit().putString(TOKEN_KEY_NAME, newToken).apply()
        }
    }

    suspend fun setMemberProfile(memberProfile: MemberProfile) {
        setProfileImageUrl(memberProfile.profileImageUrl ?: "")
        setNickname(memberProfile.nickname)
        setRecoveryCode(memberProfile.uuidCode)
    }

    suspend fun setProfileImageUrl(url: String) {
        withContext(Dispatchers.IO) {
            mUserInfoPrefs.edit().putString(PROFILE_IMAGE_URL_KEY_NAME, url).apply()
        }
    }

    private suspend fun setNickname(nickname: String) {
        withContext(Dispatchers.IO) {
            mUserInfoPrefs.edit().putString(NICKNAME_KEY_NAME, nickname).apply()
        }
    }

    private suspend fun setRecoveryCode(code: String) {
        withContext(Dispatchers.IO) {
            mUserInfoPrefs.edit().putString(RECOVERY_CODE_KEY_NAME, code).apply()
        }
    }

    companion object {
        private const val USER_INFO_PREF_NAME = "com.on.staccato.user_info_prefs"
        private const val TOKEN_KEY_NAME = "com.on.staccato.token"
        private const val PROFILE_IMAGE_URL_KEY_NAME = "com.on.staccato.profile"
        private const val NICKNAME_KEY_NAME = "com.on.staccato.nickname"
        private const val RECOVERY_CODE_KEY_NAME = "com.on.staccato.recovery"
        const val EMPTY_STRING = ""
    }
}
