package com.on.staccato.data.member

import android.content.SharedPreferences
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.model.MemberProfile.Companion.EMPTY_STRING
import com.on.staccato.domain.model.MemberProfile.Companion.INVALID_MEMBER_ID
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

        override suspend fun updateToken(newToken: String) {
            withContext(Dispatchers.IO) {
                userInfoPrefs.edit().putString(TOKEN_KEY_NAME, newToken).apply()
            }
        }

        override suspend fun getMemberProfile(): MemberProfile =
            MemberProfile(
                memberId = getMemberId(),
                profileImageUrl = getProfileImageUrl(),
                nickname = getNickname(),
                uuidCode = getRecoveryCode(),
            )

        override suspend fun updateMemberProfile(memberProfile: MemberProfile) {
            withContext(Dispatchers.IO) {
                userInfoPrefs.edit()
                    .putLong(MEMBER_ID_KEY_NAME, memberProfile.memberId)
                    .putString(PROFILE_IMAGE_URL_KEY_NAME, memberProfile.profileImageUrl)
                    .putString(NICKNAME_KEY_NAME, memberProfile.nickname)
                    .putString(RECOVERY_CODE_KEY_NAME, memberProfile.uuidCode)
                    .apply()
            }
        }

        override suspend fun updateProfileImageUrl(url: String?) {
            withContext(Dispatchers.IO) {
                userInfoPrefs.edit().putString(PROFILE_IMAGE_URL_KEY_NAME, url ?: EMPTY_STRING).apply()
            }
        }

        private suspend fun getMemberId(): Long =
            withContext(Dispatchers.IO) {
                if (userInfoPrefs.contains(MEMBER_ID_KEY_NAME)) {
                    userInfoPrefs.getLong(MEMBER_ID_KEY_NAME, INVALID_MEMBER_ID)
                } else {
                    INVALID_MEMBER_ID
                }
            }

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

        companion object {
            private const val TOKEN_KEY_NAME = "com.on.staccato.token"
            private const val PROFILE_IMAGE_URL_KEY_NAME = "com.on.staccato.profile"
            private const val NICKNAME_KEY_NAME = "com.on.staccato.nickname"
            private const val RECOVERY_CODE_KEY_NAME = "com.on.staccato.recovery"
            private const val MEMBER_ID_KEY_NAME = "com.on.staccato.member_id"
        }
    }
