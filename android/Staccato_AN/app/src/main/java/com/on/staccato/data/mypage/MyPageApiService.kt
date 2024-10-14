package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.data.dto.mypage.ProfileImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyPageApiService {
    @GET(MYPAGE_PATH)
    suspend fun getMemberProfile(): Response<MemberProfileResponse>

    @Multipart
    @POST(PROFILE_IMAGE_CHANGE_PATH)
    suspend fun postProfileImageChange(
        @Part imageFile: MultipartBody.Part,
    ): Response<ProfileImageResponse>

    companion object {
        private const val MYPAGE_PATH = "/mypage"
        private const val PROFILE_IMAGE = "/images"
        private const val PROFILE_IMAGE_CHANGE_PATH = "$MYPAGE_PATH$PROFILE_IMAGE"
    }
}
