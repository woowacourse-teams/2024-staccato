package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.mypage.MyProfileResponse
import com.on.staccato.domain.model.MyProfile

fun MyProfileResponse.toDomain() =
    MyProfile(
        profileImageUrl = profileImageUrl,
        nickname = nickname,
        uuidCode = code,
    )
