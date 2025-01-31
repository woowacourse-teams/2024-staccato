package com.on.staccato.data.mypage

import com.on.staccato.domain.model.MemberProfile

interface MyPageLocalDataSource {
    suspend fun getMemberProfile(): MemberProfile

    suspend fun updateMemberProfile(memberProfile: MemberProfile)

    suspend fun updateProfileImageUrl(url: String?)
}
