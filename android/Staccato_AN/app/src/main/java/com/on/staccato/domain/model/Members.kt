package com.on.staccato.domain.model

data class Members(val members: List<Member>) {
    fun addFirst(member: Member): Members = Members((listOf(member) + members).distinct())

    fun filter(member: Member): Members = Members(members.filterNot { it.memberId == member.memberId })

    fun contains(target: Member) = members.contains(target)
}

val emptyMembers = Members(emptyList())

val dummyMembers =
    Members(
        listOf(
            dummyMember,
            longNameMember.copy(memberId = 1L),
            dummyMember.copy(memberId = 2L),
            longNameMember.copy(memberId = 3L),
            dummyMember.copy(memberId = 4L),
            longNameMember.copy(memberId = 5L),
            dummyMember.copy(memberId = 6L),
            longNameMember.copy(memberId = 7L),
        ),
    )
