package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.R
import com.woowacourse.staccato.domain.model.Feeling
import com.woowacourse.staccato.domain.model.Moment
import com.woowacourse.staccato.domain.model.VisitLog
import com.woowacourse.staccato.presentation.moment.comments.CommentUiModel
import com.woowacourse.staccato.presentation.moment.detail.MomentDetailUiModel
import com.woowacourse.staccato.presentation.moment.feeling.FeelingUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateDefaultUiModel

fun Moment.toMomentDetailUiModel(): MomentDetailUiModel {
    return MomentDetailUiModel(
        id = momentId,
        placeName = placeName,
        momentImageUrls = momentImageUrls,
        address = address,
        visitedAt = visitedAt,
    )
}

fun Moment.toVisitUpdateDefaultUiModel(): VisitUpdateDefaultUiModel {
    return VisitUpdateDefaultUiModel(
        id = momentId,
        address = address,
        visitedAt = visitedAt,
    )
}

fun VisitLog.toCommentUiModel() =
    CommentUiModel(
        id = visitLogId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

fun Feeling.toFeelingUiModel(selectedFeeling: String = Feeling.NOTHING.value): FeelingUiModel {
    val src =
        when (this) {
            Feeling.HAPPY -> R.drawable.feeling_happy
            Feeling.ANGRY -> R.drawable.feeling_angry
            Feeling.SAD -> R.drawable.feeling_sad
            Feeling.SCARED -> R.drawable.feeling_scared
            Feeling.EXCITED -> R.drawable.feeling_excited
            Feeling.NOTHING -> null
        }
    return FeelingUiModel(value, src, selectedFeeling == this.value)
}
