package com.on.staccato.presentation.map.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.on.staccato.presentation.common.color.CategoryColor
import java.time.LocalDateTime

// TODO: API 변경되면 default 값 제거하기
data class StaccatoLocationUiModel(
    val staccatoId: Long,
    val latitude: Double,
    val longitude: Double,
    val color: CategoryColor,
    val staccatoTitle: String = "스타카토 제목",
    val visitedAt: LocalDateTime = LocalDateTime.now(),
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitude, longitude)

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float? = null
}
