package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.data.network.ApiResult
import okhttp3.MultipartBody
import javax.inject.Inject

class MyPageRemoteDataSourceImpl
    @Inject
    constructor(
        private val myPageApiService: MyPageApiService,
    ) : MyPageRemoteDataSource {
        override suspend fun loadMemberProfile(): ApiResult<MemberProfileResponse> = myPageApiService.getMemberProfile()

        override suspend fun updateProfileImage(profileImageFile: MultipartBody.Part): ApiResult<ProfileImageResponse> =
            myPageApiService.postProfileImageChange(profileImageFile)
    }
