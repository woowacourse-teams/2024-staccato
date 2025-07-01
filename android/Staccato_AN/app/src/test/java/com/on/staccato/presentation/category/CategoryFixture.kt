package com.on.staccato.presentation.category

import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Members
import com.on.staccato.domain.model.Participant
import com.on.staccato.domain.model.Role
import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.invite.model.MemberUiModel
import com.on.staccato.presentation.category.invite.model.MembersUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.color.CategoryColor

const val VALID_ID = 1L
const val INVALID_ID = 0L

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

val staccatoMembers = Members(listOf(bingti, hannah, hodu))

val participants = staccatoMembers.members.map { Participant(it, Role.GUEST) }

val category =
    Category(
        categoryId = 1,
        categoryThumbnailUrl = null,
        categoryTitle = "카테고리 제목",
        startAt = null,
        endAt = null,
        description = null,
        color = CategoryColor.GRAY.label,
        participants = participants,
        staccatos = listOf(),
        isShared = false,
        myRole = Role.HOST,
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
        members = staccatoMembers.members,
        staccatos = listOf(),
        isShared = false,
        myRole = Role.HOST,
    )
