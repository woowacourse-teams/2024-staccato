package com.on.staccato.domain.model

data class Member(
    val memberId: Long,
    val nickname: String,
    val memberImage: String? = null,
)

val dummyMembers =
    listOf(
        Member(memberId = 1, nickname = "빙티", memberImage = "https://avatars.githubusercontent.com/u/46596035?v=4"),
        Member(memberId = 2, nickname = "해나", memberImage = "https://avatars.githubusercontent.com/u/103019852?v=4"),
        Member(memberId = 3, nickname = "호두"),
    )
