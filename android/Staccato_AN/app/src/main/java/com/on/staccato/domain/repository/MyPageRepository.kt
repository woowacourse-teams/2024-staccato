package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mypage.MyProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.domain.model.MyProfile
import okhttp3.MultipartBody

interface MyPageRepository {
    suspend fun getMyProfile(): ResponseResult<MyProfile>

    suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ResponseResult<ProfileImageResponse>
}
