package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.data.network.ApiResult
import okhttp3.MultipartBody

interface MyPageDataSource {
    suspend fun loadMemberProfile(): ApiResult<MemberProfileResponse>

    suspend fun updateProfileImage(profileImageFile: MultipartBody.Part): ApiResult<ProfileImageResponse>
}
