package com.on.staccato.domain.model

val bingti =
    Member(
        memberId = 0L,
        nickname = "빙나",
        memberImage = "",
    )

val hannah =
    Member(
        memberId = 1L,
        nickname = "해나",
        memberImage = "",
    )

val hodu =
    Member(
        memberId = 2L,
        nickname = "호두",
        memberImage = "",
    )

val staccatoMembers = Members(listOf(bingti, hannah, hodu))
