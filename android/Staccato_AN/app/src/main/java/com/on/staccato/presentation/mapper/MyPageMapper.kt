package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.AccountInformation
import com.on.staccato.presentation.mypage.model.AccountInformationUiModel

fun AccountInformation.toUiModel(): AccountInformationUiModel =
    AccountInformationUiModel(
        profileImageUrl = profileImageUrl,
        nickname = nickname,
        uuidCode = uuidCode,
    )
