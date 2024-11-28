package com.on.staccato.data.mypage

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.Exception
import com.on.staccato.data.ResponseResult
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
        override suspend fun getMemberProfile(): ResponseResult<MemberProfile> {
            val responseResult = handleApiResponse { myPageApiService.getMemberProfile() }
            return when (responseResult) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> {
                    val myProfile = responseResult.data.toDomain()
                    Success(myProfile)
                }
            }
        }

        override suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ResponseResult<ProfileImageResponse> {
            val responseResult =
                handleApiResponse { myPageApiService.postProfileImageChange(profileImageFile) }
            return when (responseResult) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data)
            }
        }
    }
