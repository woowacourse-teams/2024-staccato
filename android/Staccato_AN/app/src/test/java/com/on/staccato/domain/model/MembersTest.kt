package com.on.staccato.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MembersTest {
    @Test
    fun `멤버 리스트의 맨 앞에 새로운 멤버를 추가할 수 있다`() {
        // given
        val newMember =
            Member(
                memberId = 1961L,
                nickname = "새멤버",
                memberImage = "테스트Url",
            )

        // when
        val newMembers = dummyMembers.addFirst(newMember)

        // then
        val actual = newMembers.members.first()
        assertThat(actual).isEqualTo(newMember)
        assertThat(newMembers.members.size).isEqualTo(dummyMembers.members.size + 1)
    }

    @Test
    fun `특정 멤버로 필터링하면 해당 멤버를 포함하지 않는다`() {
        // given
        val target = dummyMembers.members.first()

        // when
        val filteredMembers = dummyMembers.filter(target)

        // then
        assertThat(target).isNotIn(filteredMembers.members)
    }
}
