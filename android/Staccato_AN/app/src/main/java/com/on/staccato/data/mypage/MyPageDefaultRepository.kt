package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.member.MemberDataSource
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.Success
import com.on.staccato.data.network.handle
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.repository.MyPageRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class MyPageDefaultRepository
    @Inject
    constructor(
        private val memberLocalDataSource: MemberDataSource,
        private val myPageRemoteDataSource: MyPageDataSource,
    ) : MyPageRepository {
        override suspend fun getMemberProfile(): ApiResult<MemberProfile> {
            val localProfile = memberLocalDataSource.getMemberProfile()
            return if (localProfile.isValid()) {
                Success(localProfile)
            } else {
                syncMemberProfile()
            }
        }

        private suspend fun syncMemberProfile(): ApiResult<MemberProfile> {
            return myPageRemoteDataSource.loadMemberProfile().handle {
                val serverProfile = it.toDomain()
                memberLocalDataSource.updateMemberProfile(serverProfile)
                serverProfile
            }
        }

        override suspend fun changeProfileImage(profileImageFile: MultipartBody.Part): ApiResult<String?> =
            myPageRemoteDataSource.updateProfileImage(profileImageFile).handle {
                val imageUrl = it.profileImageUrl
                memberLocalDataSource.updateProfileImageUrl(imageUrl)
                imageUrl
            }
    }
