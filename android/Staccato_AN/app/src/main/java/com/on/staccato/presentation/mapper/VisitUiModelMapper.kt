package com.on.staccato.presentation.mapper

import com.on.staccato.R
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Staccato
import com.on.staccato.presentation.staccato.comments.CommentUiModel
import com.on.staccato.presentation.staccato.detail.MomentDetailUiModel
import com.on.staccato.presentation.staccato.feeling.FeelingUiModel

fun Staccato.toMomentDetailUiModel(): MomentDetailUiModel {
    return MomentDetailUiModel(
        id = staccatoId,
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        staccatoTitle = staccatoTitle,
        placeName = placeName,
        latitude = latitude,
        longitude = longitude,
        momentImageUrls = staccatoImageUrls,
        address = address,
        visitedAt = visitedAt,
    )
}

fun Comment.toCommentUiModel() =
    CommentUiModel(
        id = commentId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

fun Feeling.toFeelingUiModel(selectedFeeling: String = Feeling.NOTHING.value): FeelingUiModel {
    val colorAndGraySrc =
        when (this) {
            Feeling.HAPPY -> R.drawable.feeling_happy to R.drawable.feeling_happy_gray
            Feeling.ANGRY -> R.drawable.feeling_angry to R.drawable.feeling_angry_gray
            Feeling.SAD -> R.drawable.feeling_sad to R.drawable.feeling_sad_gray
            Feeling.SCARED -> R.drawable.feeling_scared to R.drawable.feeling_scared_gray
            Feeling.EXCITED -> R.drawable.feeling_excited to R.drawable.feeling_excited_gray
            Feeling.NOTHING -> null
        }
    return FeelingUiModel(
        value,
        colorAndGraySrc?.first,
        colorAndGraySrc?.second,
        selectedFeeling == this.value,
    )
}
