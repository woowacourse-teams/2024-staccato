package com.on.staccato.presentation.category.invite

import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.invite.model.MemberUiModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemberUiModelTest {
    @Test
    fun `기본 상태는 '선택되지 않음' 이다`() {
        val bingti = MemberUiModel(member = memberBingTi)

        assertThat(bingti.inviteState).isEqualTo(InviteState.UNSELECTED)
    }

    @Test
    fun `선택됨 상태로 바꿀 수 있다`() {
        val bingti = memberUiModelBingTi.changeState(InviteState.SELECTED)

        assertThat(bingti.inviteState).isEqualTo(InviteState.SELECTED)
    }

    @Test
    fun `참여중 상태로 바꿀 수 있다`() {
        val bingti = memberUiModelBingTi.changeState(InviteState.PARTICIPATING)

        assertThat(bingti.inviteState).isEqualTo(InviteState.PARTICIPATING)
    }
}
