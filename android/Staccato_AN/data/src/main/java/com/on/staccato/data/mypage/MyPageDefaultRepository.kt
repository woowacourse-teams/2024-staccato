package com.on.staccato.data.mypage

import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.member.MemberDataSource
import com.on.staccato.data.network.handle
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.Success
import com.on.staccato.domain.UploadFile
import com.on.staccato.domain.model.MemberProfile
import com.on.staccato.domain.repository.MyPageRepository
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

        override suspend fun changeProfileImage(file: UploadFile): ApiResult<String?> =
            myPageRemoteDataSource.updateProfileImage(file).handle {
                val imageUrl = it.profileImageUrl
                memberLocalDataSource.updateProfileImageUrl(imageUrl)
                imageUrl
            }
    }
