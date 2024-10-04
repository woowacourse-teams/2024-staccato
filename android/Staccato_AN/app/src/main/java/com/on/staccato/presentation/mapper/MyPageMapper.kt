package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.MyProfile
import com.on.staccato.presentation.mypage.model.MyProfileUiModel

fun MyProfile.toUiModel(): MyProfileUiModel =
    MyProfileUiModel(
        profileImageUrl = profileImageUrl,
        nickname = nickname,
        uuidCode = uuidCode,
    )
