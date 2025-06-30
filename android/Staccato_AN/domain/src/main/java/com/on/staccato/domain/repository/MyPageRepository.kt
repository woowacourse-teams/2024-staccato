package com.on.staccato.domain.repository

import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.UploadFile
import com.on.staccato.domain.model.MemberProfile

interface MyPageRepository {
    suspend fun getMemberProfile(): ApiResult<MemberProfile>

    suspend fun changeProfileImage(file: UploadFile): ApiResult<String?>
}
