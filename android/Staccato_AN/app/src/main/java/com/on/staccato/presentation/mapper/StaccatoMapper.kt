package com.on.staccato.presentation.mapper

import com.on.staccato.R
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoMarker
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.staccato.comments.CommentUiModel
import com.on.staccato.presentation.staccato.detail.StaccatoDetailUiModel
import com.on.staccato.presentation.staccato.feeling.FeelingUiModel

fun Staccato.toStaccatoDetailUiModel(): StaccatoDetailUiModel {
    return StaccatoDetailUiModel(
        id = staccatoId,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        staccatoTitle = staccatoTitle,
        placeName = placeName,
        latitude = latitude,
        longitude = longitude,
        staccatoImageUrls = staccatoImageUrls,
        address = address,
        visitedAt = visitedAt,
    )
}

fun Comment.toCommentUiModel(isMine: Boolean) =
    CommentUiModel(
        id = commentId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
        isMine = isMine,
    )

fun Feeling.toFeelingUiModel(selectedFeeling: String = Feeling.NOTHING.value): FeelingUiModel {
    val colorAndGraySrc =
        when (this) {
            Feeling.HAPPY -> R.drawable.feeling_happy_note to R.drawable.feeling_happy_note_gray
            Feeling.ANGRY -> R.drawable.feeling_angry_note to R.drawable.feeling_angry_note_gray
            Feeling.SAD -> R.drawable.feeling_sad_note to R.drawable.feeling_sad_note_gray
            Feeling.SCARED -> R.drawable.feeling_scared_note to R.drawable.feeling_scared_note_gray
            Feeling.EXCITED -> R.drawable.feeling_excited_note to R.drawable.feeling_excited_note_gray
            Feeling.NOTHING -> null
        }
    return FeelingUiModel(
        value,
        colorAndGraySrc?.first,
        colorAndGraySrc?.second,
        selectedFeeling == this.value,
    )
}

fun StaccatoMarker.toUiModel() =
    StaccatoMarkerUiModel(
        staccatoId = staccatoId,
        latitude = latitude,
        longitude = longitude,
        color = CategoryColor.getCategoryColorBy(color),
        staccatoTitle = title,
        visitedAt = visitedAt,
    )
