package com.on.staccato.data.invitation

import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.Success
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation
import com.on.staccato.domain.repository.InvitationRepository

class InvitationTempRepository(
    private val receivedInvitations: List<ReceivedInvitation> = dummyReceivedInvitations,
    private val sentInvitations: List<SentInvitation> = dummySentInvitations,
): InvitationRepository {
    override fun getReceivedInvitations(): ApiResult<List<ReceivedInvitation>> = Success(receivedInvitations)
    override fun getSentInvitations(): ApiResult<List<SentInvitation>> = Success(sentInvitations)
}

private val dummyReceivedInvitations =
    listOf(
        ReceivedInvitation(0L, "호두의 대구 나들이", Member(100L, "호두", null)),
        ReceivedInvitation(1L, "호티의 한잔줍쇼", Member(101L, "호티", null)),
        ReceivedInvitation(2L, "해나의 서울 맛집 탐방", Member(102L, "해나", null)),
        ReceivedInvitation(3L, "클라이밍 갈 사람 여기여기 붙어라~~~~~~!!!!!!", Member(104L, "리니", null)),
        ReceivedInvitation(4L, "빙티라미수케잌", Member(103L, "빙티", null)),
        ReceivedInvitation(5L, "그럼 그냥 이거 하지 말자", Member(105L, "폭포", null)),
        ReceivedInvitation(6L, "카ㅏㅏㅏㅏㅏㅏ고!!!", Member(106L, "카고", null)),
        ReceivedInvitation(7L, "안줄리나 줄리야?", Member(107L, "줄리", null)),
        ReceivedInvitation(8L, "디자인 그러케 하는거 아닌데영", Member(108L, "케영", null)),
        ReceivedInvitation(9L, "영미!!!!!!!", Member(109L, "영미", null)),
    )

private val dummySentInvitations =
    listOf(
        SentInvitation(3L, "클라이밍 갈 사람 여기여기 붙어라~~~~~~!!!!!!", Member(104L, "리니", null)),
        SentInvitation(1L, "호티의 한잔줍쇼", Member(101L, "호티", null)),
        SentInvitation(0L, "호두의 대구 나들이", Member(100L, "호두", null)),
        SentInvitation(8L, "디자인 그러케 하는거 아닌데영", Member(108L, "케영", null)),
        SentInvitation(5L, "그럼 그냥 이거 하지 말자", Member(105L, "폭포", null)),
        SentInvitation(2L, "해나의 서울 맛집 탐방", Member(102L, "해나", null)),
        SentInvitation(6L, "카ㅏㅏㅏㅏㅏㅏ고!!!", Member(106L, "카고", null)),
        SentInvitation(4L, "빙티라미수케잌", Member(103L, "빙티", null)),
        SentInvitation(9L, "영미!!!!!!!", Member(109L, "영미", null)),
        SentInvitation(7L, "안줄리나 줄리야?", Member(107L, "줄리", null)),
    )
