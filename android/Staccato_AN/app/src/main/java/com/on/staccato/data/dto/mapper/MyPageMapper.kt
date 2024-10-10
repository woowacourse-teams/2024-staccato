package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.mypage.AccountInformationResponse
import com.on.staccato.domain.model.AccountInformation

fun AccountInformationResponse.toDomain() =
    AccountInformation(
        profileImageUrl = profileImageUrl,
        nickname = nickname,
        uuidCode = code,
    )
