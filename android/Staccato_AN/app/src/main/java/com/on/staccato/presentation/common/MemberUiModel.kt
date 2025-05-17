package com.on.staccato.presentation.common

data class MemberUiModel(
    val id: Long,
    val nickname: String,
    val memberImage: String? = null,
)

val dummyMembersUiModel =
    listOf(
        MemberUiModel(id = 1, nickname = "빙티", memberImage = "https://avatars.githubusercontent.com/u/46596035?v=4"),
        MemberUiModel(id = 2, nickname = "해나", memberImage = "https://avatars.githubusercontent.com/u/103019852?v=4"),
        MemberUiModel(id = 3, nickname = "호두"),
    )
