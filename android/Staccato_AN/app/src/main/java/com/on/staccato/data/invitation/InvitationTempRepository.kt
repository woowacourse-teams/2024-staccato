package com.on.staccato.data.invitation

import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.Success
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation
import com.on.staccato.domain.repository.InvitationRepository
import javax.inject.Inject

class InvitationTempRepository
    @Inject
    constructor() : InvitationRepository {
        override suspend fun getReceivedInvitations(): ApiResult<List<ReceivedInvitation>> = Success(dummyReceivedInvitations)

        override suspend fun acceptInvitation(invitationId: Long): ApiResult<Unit> = Success(Unit)

        override suspend fun rejectInvitation(invitationId: Long): ApiResult<Unit> = Success(Unit)

        override suspend fun getSentInvitations(): ApiResult<List<SentInvitation>> = Success(dummySentInvitations)

        override suspend fun cancelInvitation(invitationId: Long): ApiResult<Unit> = Success(Unit)
    }

private val dummyReceivedInvitations =
    listOf(
        ReceivedInvitation(0L, Member(100L, "호두", null), 0L, "호두의 대구 나들이"),
        ReceivedInvitation(1L, Member(101L, "호티", null), 1L, "호티의 한잔줍쇼"),
        ReceivedInvitation(2L, Member(102L, "해나", null), 2L, "해나의 서울 맛집 탐방"),
        ReceivedInvitation(3L, Member(104L, "리니", null), 3L, "클라이밍 갈 사람 여기여기 붙어라~~~~~~!!!!!!"),
        ReceivedInvitation(4L, Member(103L, "빙티", null), 4L, "빙티라미수케잌"),
        ReceivedInvitation(5L, Member(105L, "폭포", null), 5L, "그럼 그냥 이거 하지 말자"),
        ReceivedInvitation(6L, Member(106L, "카고", null), 6L, "카ㅏㅏㅏㅏㅏㅏ고!!!"),
        ReceivedInvitation(7L, Member(107L, "줄리", null), 7L, "안줄리나 줄리야?"),
        ReceivedInvitation(8L, Member(108L, "케영", null), 8L, "디자인 그러케 하는거 아닌데영"),
        ReceivedInvitation(9L, Member(109L, "영미", null), 9L, "영미!!!!!!!"),
    )

private val dummySentInvitations =
    listOf(
        SentInvitation(10L, Member(110L, "테스트", null), 10L, "무지무지무지무지무지무지무지무지무지무지 기이이이이인 제목"),
        SentInvitation(3L, Member(104L, "리니", null), 3L, "클라이밍 갈 사람 여기여기 붙어라~~~~~~!!!!!!"),
        SentInvitation(1L, Member(101L, "호티", null), 1L, "호티의 한잔줍쇼"),
        SentInvitation(0L, Member(100L, "호두", null), 0L, "호두의 대구 나들이"),
        SentInvitation(8L, Member(108L, "케영", null), 8L, "디자인 그러케 하는거 아닌데영"),
        SentInvitation(5L, Member(105L, "폭포", null), 5L, "그럼 그냥 이거 하지 말자"),
        SentInvitation(2L, Member(102L, "해나", null), 2L, "해나의 서울 맛집 탐방"),
        SentInvitation(6L, Member(106L, "카고", null), 6L, "카ㅏㅏㅏㅏㅏㅏ고!!!"),
        SentInvitation(4L, Member(103L, "빙티", null), 4L, "빙티라미수케잌"),
        SentInvitation(9L, Member(109L, "영미", null), 9L, "영미!!!!!!!"),
        SentInvitation(7L, Member(107L, "줄리", null), 7L, "안줄리나 줄리야?"),
    )
