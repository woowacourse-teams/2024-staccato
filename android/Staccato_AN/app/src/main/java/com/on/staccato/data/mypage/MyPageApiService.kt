package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import com.on.staccato.data.network.ApiResult
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyPageApiService {
    // TODO: 사용자 프로필 조회 API V2 머지 되면 수정
    @GET(MY_PAGE_PATH)
    suspend fun getMemberProfile(): ApiResult<MemberProfileResponse>

    @Multipart
    @POST(PROFILE_IMAGE_CHANGE_PATH)
    suspend fun postProfileImageChange(
        @Part imageFile: MultipartBody.Part,
    ): ApiResult<ProfileImageResponse>

    companion object {
        private const val MY_PAGE_PATH = "/mypage"
        private const val MY_PAGE_PATH_V2 = "/v2${MY_PAGE_PATH}"
        private const val PROFILE_IMAGE = "/images"
        private const val PROFILE_IMAGE_CHANGE_PATH = "$MY_PAGE_PATH$PROFILE_IMAGE"
    }
}
