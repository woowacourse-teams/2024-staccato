package com.on.staccato.data.mypage

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.handle
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.repository.MyPageRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class MyPageDefaultRepository
    @Inject
    constructor(
        private val myPageRemoteDataSource: MyPageRemoteDataSource,
    ) : MyPageRepository {
        override suspend fun getMemberProfile(): ApiResult<MemberProfile> =
            myPageRemoteDataSource.loadMemberProfile().handle { it.toDomain() }

        override suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ApiResult<String?> =
            myPageRemoteDataSource.setProfileImageUrl(profileImageFile).handle { it.profileImageUrl }
    }
