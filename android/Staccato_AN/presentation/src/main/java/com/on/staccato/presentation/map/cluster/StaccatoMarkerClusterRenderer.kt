package com.on.staccato.presentation.map.cluster

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel

class StaccatoMarkerClusterRenderer(
    context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<StaccatoMarkerUiModel>?,
    private val clusterDrawManager: ClusterDrawManager,
) : DefaultClusterRenderer<StaccatoMarkerUiModel>(context, map, clusterManager) {
    override fun onBeforeClusterRendered(
        cluster: Cluster<StaccatoMarkerUiModel>,
        markerOptions: MarkerOptions,
    ) {
        val icon: BitmapDescriptor = clusterDrawManager.generateClusterIcon(cluster.size)
        markerOptions.icon(icon)
    }

    override fun onClusterUpdated(
        cluster: Cluster<StaccatoMarkerUiModel>,
        marker: Marker,
    ) {
        val icon: BitmapDescriptor = clusterDrawManager.generateClusterIcon(cluster.size)
        marker.setIcon(icon)
    }

    override fun onBeforeClusterItemRendered(
        item: StaccatoMarkerUiModel,
        markerOptions: MarkerOptions,
    ) {
        val icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(item.color.markerResWithShadow)
        markerOptions.icon(icon)
    }
}
