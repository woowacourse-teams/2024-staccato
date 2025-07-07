package com.on.staccato.data.member

import com.on.staccato.domain.model.MemberProfile

interface MemberDataSource {
    suspend fun getToken(): String?

    suspend fun updateToken(newToken: String)

    suspend fun getMemberProfile(): MemberProfile

    suspend fun updateMemberProfile(memberProfile: MemberProfile)

    suspend fun updateProfileImageUrl(url: String?)
}
