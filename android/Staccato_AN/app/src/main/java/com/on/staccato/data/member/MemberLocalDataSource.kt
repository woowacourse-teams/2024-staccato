package com.on.staccato.data.member

import android.content.SharedPreferences
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.model.MemberProfile.Companion.EMPTY_STRING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MemberLocalDataSource
    @Inject
    constructor(
        private val userInfoPrefs: SharedPreferences,
    ) : MemberDataSource {
        override suspend fun getToken(): String? =
            withContext(Dispatchers.IO) {
                userInfoPrefs.getString(TOKEN_KEY_NAME, EMPTY_STRING)
            }

        override suspend fun getMemberId(): Long =
            withContext(Dispatchers.IO) {
                userInfoPrefs.getLong(MEMBER_ID_KEY_NAME, 0L)
            }

        override suspend fun setTokenAndId(
            newToken: String,
            id: Long,
        ) {
            withContext(Dispatchers.IO) {
                userInfoPrefs.edit()
                    .putString(TOKEN_KEY_NAME, newToken)
                    .putLong(MEMBER_ID_KEY_NAME, id)
                    .apply()
            }
        }

        override suspend fun getMemberProfile(): MemberProfile =
            MemberProfile(
                profileImageUrl = getProfileImageUrl(),
                nickname = getNickname(),
                uuidCode = getRecoveryCode(),
            )

        private suspend fun getProfileImageUrl(): String? {
            return withContext(Dispatchers.IO) {
                userInfoPrefs.getString(PROFILE_IMAGE_URL_KEY_NAME, null)
            }
        }

        private suspend fun getNickname(): String {
            return withContext(Dispatchers.IO) {
                userInfoPrefs.getString(NICKNAME_KEY_NAME, null) ?: EMPTY_STRING
            }
        }

        private suspend fun getRecoveryCode(): String {
            return withContext(Dispatchers.IO) {
                userInfoPrefs.getString(RECOVERY_CODE_KEY_NAME, null) ?: EMPTY_STRING
            }
        }

        override suspend fun updateMemberProfile(memberProfile: MemberProfile) {
            updateProfileImageUrl(memberProfile.profileImageUrl)
            updateNickname(memberProfile.nickname)
            updateRecoveryCode(memberProfile.uuidCode)
        }

        override suspend fun updateProfileImageUrl(url: String?) {
            withContext(Dispatchers.IO) {
                userInfoPrefs.edit().putString(PROFILE_IMAGE_URL_KEY_NAME, url ?: EMPTY_STRING).apply()
            }
        }

        private suspend fun updateNickname(nickname: String) {
            withContext(Dispatchers.IO) {
                userInfoPrefs.edit().putString(NICKNAME_KEY_NAME, nickname).apply()
            }
        }

        private suspend fun updateRecoveryCode(code: String) {
            withContext(Dispatchers.IO) {
                userInfoPrefs.edit().putString(RECOVERY_CODE_KEY_NAME, code).apply()
            }
        }

        companion object {
            private const val TOKEN_KEY_NAME = "com.on.staccato.token"
            private const val PROFILE_IMAGE_URL_KEY_NAME = "com.on.staccato.profile"
            private const val NICKNAME_KEY_NAME = "com.on.staccato.nickname"
            private const val RECOVERY_CODE_KEY_NAME = "com.on.staccato.recovery"
            private const val MEMBER_ID_KEY_NAME = "com.on.staccato.member_id"
        }
    }
