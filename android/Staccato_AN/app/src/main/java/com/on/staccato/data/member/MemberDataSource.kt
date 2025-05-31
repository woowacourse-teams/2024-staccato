package com.on.staccato.data.member

import com.on.staccato.domain.model.MemberProfile

interface MemberDataSource {
    suspend fun getToken(): String?

    suspend fun getMemberId(): Long

    suspend fun setTokenAndId(
        newToken: String,
        id: Long,
    )

    suspend fun getMemberProfile(): MemberProfile

    suspend fun updateMemberProfile(memberProfile: MemberProfile)

    suspend fun updateProfileImageUrl(url: String?)
}
