package com.on.staccato.presentation.category.invite.model

import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.dummyMember
import com.on.staccato.domain.model.longNameMember

data class MemberUiModel(
    val member: Member,
    val inviteState: InviteState = InviteState.UNSELECTED,
) {
    fun changeState(newState: InviteState) = copy(inviteState = newState)
}

fun Member.toUiModel() =
    MemberUiModel(
        member = this,
    )

val dummyMemberUiModel = MemberUiModel(dummyMember)

val longNameMemberUiModel = MemberUiModel(longNameMember)

val selectedMemberUiModel = MemberUiModel(dummyMember).changeState(InviteState.SELECTED)

val participatingMemberUiModel = MemberUiModel(dummyMember).changeState(InviteState.PARTICIPATING)
