package com.on.staccato.data.mypage

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.domain.model.AccountInformation
import com.on.staccato.domain.repository.MyPageRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class MyPageDefaultRepository
    @Inject
    constructor(
        private val myPageApiService: MyPageApiService,
    ) : MyPageRepository {
        override suspend fun getAccountInformation(): ResponseResult<AccountInformation> {
            val responseResult = handleApiResponse { myPageApiService.getAccountInformation() }
            return when (responseResult) {
                is ResponseResult.Exception ->
                    ResponseResult.Exception(
                        responseResult.e,
                        ERROR_MESSAGE_GET_PROFILE_FAIL,
                    )

                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> {
                    val myProfile = responseResult.data.toDomain()
                    ResponseResult.Success(myProfile)
                }
            }
        }

        override suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ResponseResult<ProfileImageResponse> {
            val responseResult =
                handleApiResponse { myPageApiService.postProfileImageChange(profileImageFile) }
            return when (responseResult) {
                is ResponseResult.Exception ->
                    ResponseResult.Exception(
                        responseResult.e,
                        ERROR_MESSAGE_CHANGE_PROFILE_IMAGE_FAIL,
                    )

                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
            }
        }

        companion object {
            const val ERROR_MESSAGE_GET_PROFILE_FAIL = "프로필 정보를 가져올 수 없습니다."
            const val ERROR_MESSAGE_CHANGE_PROFILE_IMAGE_FAIL = "프로필 이미지 변경에 실패했습니다."
        }
    }
