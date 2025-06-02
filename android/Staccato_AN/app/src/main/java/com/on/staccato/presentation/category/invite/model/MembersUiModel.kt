package com.on.staccato.presentation.category.invite.model

import com.on.staccato.domain.model.Members

data class MembersUiModel(val members: List<MemberUiModel>) {
    operator fun plus(newMembersUiModel: MembersUiModel): MembersUiModel = MembersUiModel(this.members + newMembersUiModel.members)

    fun changeStates(
        target: Members,
        newState: InviteState,
    ) = MembersUiModel(
        members.map {
            if (target.members.contains(it.member)) {
                it.changeState(newState)
            } else {
                it
            }
        },
    )

    companion object {
        val emptyMembersUiModel = MembersUiModel(emptyList())
    }
}

fun Members.toUiModel() = MembersUiModel(members = members.map { it.toUiModel() })

val dummyMembersUiModel =
    MembersUiModel(
        listOf(
            dummyMemberUiModel,
            dummyMemberUiModel.copy(inviteState = InviteState.SELECTED),
            dummyMemberUiModel.copy(inviteState = InviteState.PARTICIPATING),
            longNameMemberUiModel,
            longNameMemberUiModel.copy(inviteState = InviteState.SELECTED),
            longNameMemberUiModel.copy(inviteState = InviteState.PARTICIPATING),
        ),
    )
