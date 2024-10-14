package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.domain.model.MemberProfile
import okhttp3.MultipartBody

interface MyPageRepository {
    suspend fun getMemberProfile(): ResponseResult<MemberProfile>

    suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ResponseResult<ProfileImageResponse>
}
