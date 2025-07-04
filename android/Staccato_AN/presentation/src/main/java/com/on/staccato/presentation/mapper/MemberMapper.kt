package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Members
import com.on.staccato.presentation.category.invite.model.MemberUiModel
import com.on.staccato.presentation.category.invite.model.MembersUiModel

fun Member.toUiModel() =
    MemberUiModel(
        member = this,
    )

fun Members.toUiModel() = MembersUiModel(members = members.map { it.toUiModel() })
