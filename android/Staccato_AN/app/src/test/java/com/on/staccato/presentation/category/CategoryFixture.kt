package com.on.staccato.presentation.category

import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Members
import com.on.staccato.domain.model.dummyMembers
import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.invite.model.MemberUiModel
import com.on.staccato.presentation.category.invite.model.MembersUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.common.color.CategoryColor

const val VALID_ID = 1L
const val INVALID_ID = 0L

val category =
    Category(
        categoryId = 1,
        categoryThumbnailUrl = null,
        categoryTitle = "카테고리 제목",
        startAt = null,
        endAt = null,
        description = null,
        color = CategoryColor.GRAY.label,
        mates = dummyMembers.members,
        staccatos = listOf(),
    )

val categoryUiModel =
    CategoryUiModel(
        id = 1,
        title = "카테고리 제목",
        categoryThumbnailUrl = null,
        startAt = null,
        endAt = null,
        description = null,
        color = CategoryColor.GRAY.label,
        members = dummyMembers.members,
        staccatos = listOf(),
    )

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
val nana =
    Member(
        memberId = 4L,
        nickname = "나나",
        memberImage = "",
    )

val staccatoMembers = Members(listOf(bingti, hannah, hodu))

val naMembers = Members(listOf(hannah, nana))

val naMembersUiModel =
    MembersUiModel(
        listOf(
            MemberUiModel(hannah, InviteState.PARTICIPATING),
            MemberUiModel(nana, InviteState.UNSELECTED),
        ),
    )

val selectedNaMembersUiModel =
    MembersUiModel(
        listOf(
            MemberUiModel(hannah, InviteState.PARTICIPATING),
            MemberUiModel(nana, InviteState.SELECTED),
        ),
    )
