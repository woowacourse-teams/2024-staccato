package com.on.staccato.presentation.map.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.on.staccato.presentation.color.CategoryColor
import java.time.LocalDateTime

data class StaccatoMarkerUiModel(
    val staccatoId: Long,
    val latitude: Double,
    val longitude: Double,
    val color: CategoryColor,
    val staccatoTitle: String,
    val visitedAt: LocalDateTime,
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitude, longitude)

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float? = null
}

val dummyStaccatoMarkerUiModel =
    StaccatoMarkerUiModel(
        staccatoId = 1L,
        latitude = 37.554677038139815,
        longitude = 126.97061201084968,
        color = CategoryColor.INDIGO,
        staccatoTitle = "스타카토 제목",
        visitedAt = LocalDateTime.now(),
    )

val dummyStaccatoMarkerUiModels =
    listOf(
        dummyStaccatoMarkerUiModel,
        dummyStaccatoMarkerUiModel.copy(
            staccatoId = 2L,
            staccatoTitle = "아주아주아주아주아주아주아주아주아주아주긴이름을가진스타카토",
        ),
    )
