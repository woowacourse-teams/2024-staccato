package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult
import com.on.staccato.domain.model.MemberProfile
import okhttp3.MultipartBody

interface MyPageRepository {
    suspend fun getMemberProfile(): ApiResult<MemberProfile>

    suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ApiResult<String?>
}
