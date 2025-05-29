package com.on.staccato.presentation.category.invite

import com.on.staccato.domain.model.Member
import com.on.staccato.presentation.category.invite.model.MemberUiModel

internal val memberBingTi =
    Member(
        memberId = 1L,
        nickname = "빙티",
        memberImage = "",
    )

internal val memberHannah =
    Member(
        memberId = 1L,
        nickname = "해나",
        memberImage = "",
    )
internal val memberHodu =
    Member(
        memberId = 1L,
        nickname = "호두",
        memberImage = "",
    )

internal val memberUiModelBingTi =
    MemberUiModel(
        member = memberBingTi,
    )

internal val memberUiModelHannah =
    MemberUiModel(
        member = memberHannah,
    )

internal val memberUiModelHodu =
    MemberUiModel(
        member = memberHodu,
    )
