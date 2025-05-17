package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Member
import com.on.staccato.presentation.common.MemberUiModel

fun Member.toUiModel() =
    MemberUiModel(
        id = memberId,
        nickname = nickname,
        memberImage = memberImage,
    )
