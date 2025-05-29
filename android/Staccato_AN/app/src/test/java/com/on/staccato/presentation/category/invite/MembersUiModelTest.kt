package com.on.staccato.presentation.category.invite

import com.on.staccato.domain.model.Members
import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.invite.model.MembersUiModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MembersUiModelTest {
    @Test
    fun `여러명의 상태를 선택됨으로 바꿀 수 있다`() {
        val originalMembers =
            listOf(
                memberUiModelBingTi,
                memberUiModelHannah,
                memberUiModelHodu,
            )
        val expectedMembers =
            listOf(
                memberUiModelBingTi,
                memberUiModelHannah.changeState(InviteState.SELECTED),
                memberUiModelHodu.changeState(InviteState.SELECTED),
            )

        val selected = Members(listOf(memberHannah, memberHodu))
        val actual = MembersUiModel(originalMembers).changeStates(selected, InviteState.SELECTED)

        assertThat(actual.members).isEqualTo(expectedMembers)
    }

    @Test
    fun `여러명의 상태를 참여중으로 바꿀 수 있다`() {
        val originalMembers =
            listOf(
                memberUiModelBingTi,
                memberUiModelHannah,
                memberUiModelHodu,
            )
        val expectedMembers =
            listOf(
                memberUiModelBingTi.changeState(InviteState.PARTICIPATING),
                memberUiModelHannah.changeState(InviteState.PARTICIPATING),
                memberUiModelHodu,
            )

        val participating = Members(listOf(memberBingTi, memberHannah))
        val actual = MembersUiModel(originalMembers).changeStates(participating, InviteState.PARTICIPATING)

        assertThat(actual.members).isEqualTo(expectedMembers)
    }

    @Test
    fun `여러명의 상태를 참여중, 선택됨으로 바꿀 수 있다`() {
        val originalMembers =
            listOf(
                memberUiModelBingTi,
                memberUiModelHannah,
                memberUiModelHodu,
            )
        val expectedMembers =
            listOf(
                memberUiModelBingTi.changeState(InviteState.PARTICIPATING),
                memberUiModelHannah.changeState(InviteState.SELECTED),
                memberUiModelHodu,
            )

        val participating = Members(listOf(memberBingTi))
        val selected = Members(listOf(memberHannah))
        val actual =
            MembersUiModel(originalMembers)
                .changeStates(participating, InviteState.PARTICIPATING)
                .changeStates(selected, InviteState.SELECTED)

        assertThat(actual.members).isEqualTo(expectedMembers)
    }
}
