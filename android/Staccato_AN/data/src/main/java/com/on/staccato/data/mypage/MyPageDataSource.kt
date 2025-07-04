package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.UploadFile

interface MyPageDataSource {
    suspend fun loadMemberProfile(): ApiResult<MemberProfileResponse>

    suspend fun updateProfileImage(uploadFile: UploadFile): ApiResult<ProfileImageResponse>
}
