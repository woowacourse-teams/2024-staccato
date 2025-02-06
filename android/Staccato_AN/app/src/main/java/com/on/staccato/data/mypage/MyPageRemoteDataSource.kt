package com.on.staccato.data.mypage

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import okhttp3.MultipartBody

interface MyPageRemoteDataSource {
    suspend fun loadMemberProfile(): ApiResult<MemberProfileResponse>

    suspend fun updateProfileImage(profileImageFile: MultipartBody.Part): ApiResult<ProfileImageResponse>
}
