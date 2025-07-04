package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.UploadFile
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class MyPageRemoteDataSource
    @Inject
    constructor(
        private val myPageApiService: MyPageApiService,
    ) : MyPageDataSource {
        override suspend fun loadMemberProfile(): ApiResult<MemberProfileResponse> = myPageApiService.getMemberProfile()

        override suspend fun updateProfileImage(uploadFile: UploadFile): ApiResult<ProfileImageResponse> =
            myPageApiService.postProfileImageChange(createFormData(uploadFile))

        private fun createFormData(uploadFile: UploadFile): MultipartBody.Part {
            val mediaType: MediaType? = uploadFile.contentType?.toMediaTypeOrNull()
            val requestFile: RequestBody = uploadFile.file.asRequestBody(mediaType)

            return createFormData(
                IMAGE_FORM_DATA_NAME,
                MY_PAGE_FILE_CHILD_NAME,
                requestFile,
            )
        }

        companion object {
            const val IMAGE_FORM_DATA_NAME = "imageFile"
            const val MY_PAGE_FILE_CHILD_NAME = "myPage"
        }
    }
