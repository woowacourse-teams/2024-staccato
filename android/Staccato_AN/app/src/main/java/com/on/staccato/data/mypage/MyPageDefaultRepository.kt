package com.on.staccato.data.mypage

import com.on.staccato.data.ApiResult
import com.on.staccato.data.Exception
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.repository.MyPageRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class MyPageDefaultRepository
    @Inject
    constructor(
        private val myPageApiService: MyPageApiService,
    ) : MyPageRepository {
        override suspend fun getMemberProfile(): ApiResult<MemberProfile> {
            return when (val responseResult = myPageApiService.getMemberProfile()) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> {
                    val myProfile = responseResult.data.toDomain()
                    Success(myProfile)
                }
            }
        }

        override suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ApiResult<ProfileImageResponse> {
            return when (val responseResult = myPageApiService.postProfileImageChange(profileImageFile)) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data)
            }
        }
    }
