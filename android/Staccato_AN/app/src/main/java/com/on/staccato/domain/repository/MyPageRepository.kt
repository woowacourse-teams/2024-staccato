package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.domain.model.AccountInformation
import okhttp3.MultipartBody

interface MyPageRepository {
    suspend fun getAccountInformation(): ResponseResult<AccountInformation>

    suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ResponseResult<ProfileImageResponse>
}
