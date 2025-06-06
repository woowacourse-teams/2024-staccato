package com.on.staccato.domain.model

data class Member(
    val memberId: Long,
    val nickname: String,
    val memberImage: String? = null,
)

val dummyMember =
    Member(
        memberId = 0L,
        nickname = "빙티",
        memberImage = "",
    )

val longNameMember = dummyMember.copy(nickname = "엄청나게아주매우아주매우아주매우아주매우아주매우닉네임")
