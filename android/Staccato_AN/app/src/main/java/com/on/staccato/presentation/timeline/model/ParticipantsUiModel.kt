package com.on.staccato.presentation.timeline.model

import com.on.staccato.domain.model.Member

data class ParticipantsUiModel(
    val profileImageUrls: List<String?>,
    val hiddenCount: Long,
) {
    companion object {
        private const val VISIBLE_LIMIT = 3

        fun from(
            members: List<Member>,
            totalCount: Long,
        ): ParticipantsUiModel =
            ParticipantsUiModel(
                profileImageUrls = members.map { it.memberImage },
                hiddenCount = (totalCount - VISIBLE_LIMIT).coerceAtLeast(0),
            )
    }
}

val dummyProfileImageUrls =
    listOf(
        "https://avatars.githubusercontent.com/u/46596035?v=4",
        "https://avatars.githubusercontent.com/u/103019852?v=4",
        "https://avatars.githubusercontent.com/u/92203597?v=4",
    )

private val participantsUiModel = ParticipantsUiModel(dummyProfileImageUrls, 0)

val dummyParticipantsUiModels: List<ParticipantsUiModel> =
    listOf(
        participantsUiModel.copy(hiddenCount = 5),
        participantsUiModel,
        participantsUiModel.copy(profileImageUrls = dummyProfileImageUrls.subList(0, 2)),
        participantsUiModel.copy(profileImageUrls = dummyProfileImageUrls.subList(0, 1)),
    )
